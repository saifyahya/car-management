import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Client, CreateClient, UpdateClient, UserRole } from '../../core/models';
import { ValetApiService } from '../../core/valet-api.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { LanguageService } from '../../core/language.service';
import { AuthService } from '../../core/auth.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  imports: [FormsModule, PageHeaderComponent],
  templateUrl: './clients.component.html',
  styleUrl: './clients.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClientsComponent {
  private readonly api = inject(ValetApiService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly langService = inject(LanguageService);

  readonly clients = signal<Client[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);

  // Modal State
  readonly showModal = signal(false);
  readonly editingClient = signal<Client | null>(null);

  formModel: CreateClient = {
    name: '',
    phoneNumber: '',
    email: '',
    location: '',
    isActive: true,
    username: '',
    defaultPassword: ''
  };

  constructor() {
    const user = this.auth.currentUser();
    if (!user) {
      this.auth.fetchCurrentUser().subscribe({
        next: (u) => {
          if (u.role !== UserRole.ADMIN) this.router.navigateByUrl('/dashboard');
          else this.load();
        },
        error: () => this.router.navigateByUrl('/login')
      });
    } else if (user.role !== UserRole.ADMIN) {
      this.router.navigateByUrl('/dashboard');
    } else {
      this.load();
    }
  }

  load() {
    this.loading.set(true);
    this.api.getClients().subscribe({
      next: data => {
        this.clients.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  openAddModal() {
    this.editingClient.set(null);
    this.formModel = { name: '', phoneNumber: '', email: '', location: '', isActive: true, username: '', defaultPassword: '' };
    this.showModal.set(true);
  }

  openEditModal(client: Client) {
    this.editingClient.set(client);
    this.formModel = {
      name: client.name,
      phoneNumber: client.phoneNumber || '',
      email: client.email || '',
      location: client.location || '',
      isActive: client.isActive
    };
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  isClientFormValid(): boolean {
    return !!(this.formModel.name && this.formModel.name.trim());
  }

  saveClient() {
    if (!this.formModel.name || !this.formModel.name.trim()) return;

    this.saving.set(true);
    const editing = this.editingClient();

    if (editing) {
      const updateData: UpdateClient = {
        name: this.formModel.name.trim(),
        phoneNumber: this.formModel.phoneNumber?.trim(),
        email: this.formModel.email?.trim(),
        location: this.formModel.location?.trim(),
        isActive: this.formModel.isActive
      };
      this.api.updateClient(editing.id, updateData).subscribe({
        next: () => {
          this.saving.set(false);
          this.closeModal();
          this.load();
        },
        error: () => this.saving.set(false)
      });
    } else {
      const createData: CreateClient = {
        name: this.formModel.name.trim(),
        phoneNumber: this.formModel.phoneNumber?.trim(),
        email: this.formModel.email?.trim(),
        location: this.formModel.location?.trim(),
        isActive: this.formModel.isActive ?? true,
        username: this.formModel.username?.trim(),
        defaultPassword: this.formModel.defaultPassword?.trim()
      };
      this.api.createClient(createData).subscribe({
        next: () => {
          this.saving.set(false);
          this.closeModal();
          this.load();
        },
        error: () => this.saving.set(false)
      });
    }
  }

  toggleActive(client: Client) {
    const updateData: UpdateClient = {
      name: client.name,
      phoneNumber: client.phoneNumber,
      email: client.email,
      location: client.location,
      isActive: !client.isActive
    };
    this.api.updateClient(client.id, updateData).subscribe({
      next: () => this.load()
    });
  }
}

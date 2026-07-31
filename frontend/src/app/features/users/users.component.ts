import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChangePasswordRequest, CreateUserRequest, UpdateUserRequest, UserAccountResponse, UserRole } from '../../core/models';
import { ValetApiService } from '../../core/valet-api.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { LanguageService } from '../../core/language.service';
import { AuthService } from '../../core/auth.service';
import { NotificationService } from '../../core/notification.service';

@Component({
  standalone: true,
  imports: [FormsModule, PageHeaderComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersComponent {
  private readonly api = inject(ValetApiService);
  private readonly auth = inject(AuthService);
  private readonly notify = inject(NotificationService);
  readonly langService = inject(LanguageService);

  readonly users = signal<UserAccountResponse[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);

  // Edit / Create User Modal State
  readonly showModal = signal(false);
  readonly editingUser = signal<UserAccountResponse | null>(null);
  readonly passwordError = signal<string>('');

  // Change Password Modal State
  readonly showChangePasswordModal = signal(false);
  readonly passwordUserModel = signal<UserAccountResponse | null>(null);
  readonly changePasswordError = signal<string>('');

  formModel: CreateUserRequest = {
    username: '',
    password: '',
    role: UserRole.VALET
  };

  changePasswordFormModel: ChangePasswordRequest = {
    currentPassword: '',
    newPassword: ''
  };

  readonly availableRoles = [UserRole.VALET, UserRole.MANAGER];
  private readonly PASSWORD_REGEX = /^(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;

  constructor() {
    this.load();
  }

  load() {
    this.loading.set(true);
    this.api.getUsers().subscribe({
      next: data => {
        this.users.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  openAddModal() {
    this.editingUser.set(null);
    this.passwordError.set('');
    this.formModel = { username: '', password: '', role: UserRole.VALET };
    this.showModal.set(true);
  }

  openEditModal(user: UserAccountResponse) {
    this.editingUser.set(user);
    this.passwordError.set('');
    this.formModel = { username: user.username, password: '', role: user.role };
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
    this.passwordError.set('');
  }

  openChangePasswordModal(user: UserAccountResponse) {
    this.passwordUserModel.set(user);
    this.changePasswordFormModel = { currentPassword: '', newPassword: '' };
    this.changePasswordError.set('');
    this.showChangePasswordModal.set(true);
  }

  closeChangePasswordModal() {
    this.showChangePasswordModal.set(false);
    this.changePasswordError.set('');
  }

  validatePasswordInput(): boolean {
    const pwd = this.formModel.password?.trim() || '';
    if (this.editingUser()) return true;

    if (!pwd || !this.PASSWORD_REGEX.test(pwd)) {
      this.passwordError.set(this.langService.t('errPasswordStrength'));
      return false;
    }

    this.passwordError.set('');
    return true;
  }

  validateNewPasswordInput(): boolean {
    const pwd = this.changePasswordFormModel.newPassword?.trim() || '';
    if (!pwd || !this.PASSWORD_REGEX.test(pwd)) {
      this.changePasswordError.set(this.langService.t('errPasswordStrength'));
      return false;
    }
    this.changePasswordError.set('');
    return true;
  }

  isFormValid(): boolean {
    if (!this.formModel.username || !this.formModel.username.trim()) return false;
    if (!this.editingUser()) {
      const pwd = this.formModel.password?.trim() || '';
      if (!pwd || !this.PASSWORD_REGEX.test(pwd)) return false;
    }
    return true;
  }

  saveUser() {
    if (!this.formModel.username || !this.formModel.username.trim()) return;
    if (!this.editingUser() && !this.validatePasswordInput()) return;

    this.saving.set(true);
    const editing = this.editingUser();

    if (editing) {
      const req: UpdateUserRequest = {
        username: this.formModel.username.trim(),
        role: this.formModel.role
      };
      this.api.updateUser(editing.id, req).subscribe({
        next: () => {
          this.saving.set(false);
          this.closeModal();
          this.load();
          this.notify.showSuccess(this.langService.t('toastUserSaved'));
        },
        error: (err) => {
          this.saving.set(false);
          if (err?.error?.message) {
            this.passwordError.set(err.error.message);
          }
        }
      });
    } else {
      const req: CreateUserRequest = {
        username: this.formModel.username.trim(),
        password: this.formModel.password!.trim(),
        role: this.formModel.role
      };
      this.api.createUser(req).subscribe({
        next: () => {
          this.saving.set(false);
          this.closeModal();
          this.load();
          this.notify.showSuccess(this.langService.t('toastUserSaved'));
        },
        error: (err) => {
          this.saving.set(false);
          if (err?.error?.message) {
            this.passwordError.set(err.error.message);
          }
        }
      });
    }
  }

  submitChangePassword() {
    const user = this.passwordUserModel();
    if (!user) return;
    if (!this.changePasswordFormModel.currentPassword) {
      this.changePasswordError.set(this.langService.t('errCreds'));
      return;
    }
    if (!this.validateNewPasswordInput()) return;

    this.saving.set(true);
    this.api.changeUserPassword(user.id, this.changePasswordFormModel).subscribe({
      next: () => {
        this.saving.set(false);
        this.closeChangePasswordModal();
        this.load();
        this.notify.showSuccess(this.langService.t('toastPasswordUpdated'));
      },
      error: (err) => {
        this.saving.set(false);
        if (err?.error?.message?.toLowerCase().includes('current password')) {
          this.changePasswordError.set(this.langService.t('errCurrentPasswordIncorrect'));
        } else if (err?.error?.message) {
          this.changePasswordError.set(err.error.message);
        } else {
          this.changePasswordError.set(this.langService.t('errPasswordStrength'));
        }
      }
    });
  }

  toggleActive(user: UserAccountResponse) {
    this.api.toggleUserActive(user.id).subscribe({
      next: () => this.load()
    });
  }
}

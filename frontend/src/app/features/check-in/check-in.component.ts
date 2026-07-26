import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CreateTicket } from '../../core/models';
import { ValetApiService } from '../../core/valet-api.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { LanguageService } from '../../core/language.service';

@Component({
  standalone: true,
  imports: [FormsModule, PageHeaderComponent],
  templateUrl: './check-in.component.html',
  styleUrl: './check-in.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CheckInComponent {
  private readonly api = inject(ValetApiService);
  private readonly router = inject(Router);
  readonly langService = inject(LanguageService);
  readonly saving = signal(false);
  model: CreateTicket = { visitorPhone: '', plateNumber: '', make: '', model: '', color: '', parkingLocation: '', keyLocation: '', notes: '' };

  save() {
    this.saving.set(true);
    this.api.create(this.model).subscribe({
      next: t => this.router.navigate(['/tickets', t.id]),
      error: () => this.saving.set(false)
    });
  }
}
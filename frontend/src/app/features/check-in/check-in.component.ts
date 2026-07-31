import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CreateTicket } from '../../core/models';
import { ValetApiService } from '../../core/valet-api.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { LanguageService } from '../../core/language.service';
import { NotificationService } from '../../core/notification.service';

export interface CountryCode {
  code: string;
  flag: string;
  label: string;
}

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
  private readonly notification = inject(NotificationService);
  readonly langService = inject(LanguageService);
  readonly saving = signal(false);

  selectedCountryCode = '+962';
  localPhoneNumber = '';

  readonly countryCodes: CountryCode[] = [
    { code: '+962', flag: '🇯🇴', label: 'Jordan (+962)' },
    { code: '+966', flag: '🇸🇦', label: 'Saudi Arabia (+966)' },
    { code: '+971', flag: '🇦🇪', label: 'UAE (+971)' },
    { code: '+965', flag: '🇰🇼', label: 'Kuwait (+965)' },
    { code: '+974', flag: '🇶🇦', label: 'Qatar (+974)' },
    { code: '+973', flag: '🇧🇭', label: 'Bahrain (+973)' },
    { code: '+968', flag: '🇴🇲', label: 'Oman (+968)' },
    { code: '+20', flag: '🇪🇬', label: 'Egypt (+20)' },
    { code: '+1', flag: '🇺🇸', label: 'USA/Canada (+1)' },
    { code: '+44', flag: '🇬🇧', label: 'UK (+44)' }
  ];

  model: CreateTicket = {
    visitorPhone: '',
    visitorEmail: '',
    plateNumber: '',
    make: '',
    model: '',
    color: '',
    parkingLocation: '',
    keyLocation: '',
    notes: ''
  };

  isValidPhone(): boolean {
    const clean = this.localPhoneNumber?.trim().replace(/^0+/, '');
    return /^\d{9}$/.test(clean || '');
  }

  isValidEmail(): boolean {
    if (!this.model.visitorEmail || !this.model.visitorEmail.trim()) return true;
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.model.visitorEmail.trim());
  }

  isFormValid(): boolean {
    return !!(
      this.isValidPhone() &&
      this.isValidEmail() &&
      this.model.plateNumber?.trim() &&
      this.model.make?.trim() &&
      this.model.model?.trim() &&
      this.model.color?.trim() &&
      this.model.parkingLocation?.trim() &&
      this.model.keyLocation?.trim()
    );
  }

  save() {
    if (!this.isFormValid()) return;

    this.saving.set(true);

    const cleanLocal = this.localPhoneNumber.trim().replace(/^0+/, '');
    this.model.visitorPhone = `${this.selectedCountryCode}${cleanLocal}`;

    this.api.create(this.model).subscribe({
      next: t => {
        this.notification.showSuccess(this.langService.t('msgTicketSuccess'));
        this.router.navigate(['/tickets', t.id]);
      },
      error: () => {
        this.saving.set(false);
        this.notification.showError(this.langService.t('msgTicketError'));
      }
    });
  }
}
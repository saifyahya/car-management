import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Ticket, TicketStatus } from '../../core/models';
import { ValetApiService } from '../../core/valet-api.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

export type Lang = 'en' | 'ar';

@Component({
  standalone: true,
  imports: [StatusBadgeComponent],
  templateUrl: './public-ticket.component.html',
  styleUrl: './public-ticket.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PublicTicketComponent {
  private readonly api = inject(ValetApiService);
  private readonly token = inject(ActivatedRoute).snapshot.paramMap.get('token')!;
  
  readonly ticket = signal<Ticket | null>(null);
  readonly loading = signal(false);

  readonly lang = signal<Lang>(
    (sessionStorage.getItem('valet_guest_lang') as Lang) || 'en'
  );

  readonly translations = {
    en: {
      eyebrow: 'HOTEL VALET',
      parkedTitle: 'Ready to leave?',
      parkedSubtitle: 'Request your vehicle before you reach the entrance.',
      requestBtn: 'Request my vehicle',
      requestingBtn: 'Requesting...',
      requestedTitle: 'Request received',
      requestedSubtitle: 'The valet team has your request in the queue.',
      retrievingTitle: 'On the way',
      retrievingSubtitle: 'A valet employee is retrieving your vehicle.',
      readyTitle: 'Your vehicle is ready',
      readySubtitle: 'Please present pickup PIN',
      deliveredTitle: 'Thank you',
      deliveredSubtitle: 'Your vehicle has been delivered.',
      ticketLabel: 'Ticket',
      autoRefresh: 'Status refreshes automatically',
      loadingText: 'Loading ticket...'
    },
    ar: {
      eyebrow: 'خدمة صف السيارات',
      parkedTitle: 'جاهز للمغادرة؟',
      parkedSubtitle: 'اطُلب مركبتك قبل الوصول إلى المدخل الرئيسي.',
      requestBtn: 'طلب مركبتي',
      requestingBtn: 'جاري إرسال الطلب...',
      requestedTitle: 'تم استلام الطلب',
      requestedSubtitle: 'طلبك الآن في قائمة الانتظار لدى فريق الفاليه.',
      retrievingTitle: 'في الطريق إليك',
      retrievingSubtitle: 'يقوم موظف خدمة الفاليه بإحضار مركبتك.',
      readyTitle: 'مركبتك جاهزة',
      readySubtitle: 'يرجى تقديم رمز الاستلام التالي عند الوصول',
      deliveredTitle: 'شكراً لك',
      deliveredSubtitle: 'تم تسليم المركبة بنجاح.',
      ticketLabel: 'تذكرة رقم',
      autoRefresh: 'يتم تحديث الحالة تلقائياً',
      loadingText: 'جاري تحميل التذكرة...'
    }
  };

  constructor() {
    this.load();
    setInterval(() => this.load(), 5000);
  }

  toggleLang() {
    const next = this.lang() === 'en' ? 'ar' : 'en';
    this.lang.set(next);
    sessionStorage.setItem('valet_guest_lang', next);
  }

  getStatusLabel(status: TicketStatus): string {
    const isAr = this.lang() === 'ar';
    switch (status) {
      case 'PARKED': return isAr ? 'مركونة' : 'Parked';
      case 'REQUESTED': return isAr ? 'مطلوبة' : 'Requested';
      case 'ASSIGNED': return isAr ? 'تم التكليف' : 'Assigned';
      case 'RETRIEVING': return isAr ? 'جاري الإحضار' : 'Retrieving';
      case 'READY': return isAr ? 'جاهزة' : 'Ready';
      case 'DELIVERED': return isAr ? 'تم التسليم' : 'Delivered';
      case 'CANCELLED': return isAr ? 'ملغاة' : 'Cancelled';
      default: return status;
    }
  }

  load(): void {
    this.api.publicGet(this.token).subscribe((ticket) => this.ticket.set(ticket));
  }

  request(): void {
    this.loading.set(true);
    this.api.request(this.token).subscribe({
      next: (ticket) => {
        this.ticket.set(ticket);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
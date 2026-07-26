import { Injectable, signal } from '@angular/core';
import { TicketStatus } from './models';

export type Lang = 'en' | 'ar';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  readonly lang = signal<Lang>(
    (localStorage.getItem('valet_staff_lang') as Lang) || 'en'
  );

  toggleLang() {
    const next = this.lang() === 'en' ? 'ar' : 'en';
    this.lang.set(next);
    localStorage.setItem('valet_staff_lang', next);
  }

  translateStatus(status: TicketStatus): string {
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

  readonly dict = {
    en: {
      brandTitle: 'V',
      brandSubtitle: 'Valet',
      dashboardNav: 'Dashboard',
      checkInNav: 'Check in vehicle',
      signOut: 'Sign out',

      // Dashboard
      dashTitle: 'Live dashboard',
      dashSubtitle: 'Manage arrivals and retrieval requests',
      dashCheckInBtn: '+ Check in vehicle',
      statActive: 'Active',
      statParked: 'Parked',
      statRequested: 'Requested',
      statRetrieving: 'Retrieving',
      statReady: 'Ready',
      queueTitle: 'Vehicle queue',
      refreshBtn: 'Refresh',
      thTicket: 'Ticket',
      thVehicle: 'Vehicle',
      thPlate: 'Plate',
      thLocation: 'Location',
      thStatus: 'Status',
      openBtn: 'Open',
      noVehicles: 'No vehicles yet.',

      // Check-in
      checkInTitle: 'Vehicle check-in',
      checkInSub: 'Create a digital valet ticket',
      secVisitor: 'Visitor and vehicle',
      lblPhone: 'Visitor phone',
      lblPlate: 'Plate number',
      lblMake: 'Make',
      lblModel: 'Model',
      lblColor: 'Color',
      secParking: 'Parking details',
      lblParkingLoc: 'Parking location',
      lblKeyLoc: 'Key location',
      lblNotes: 'Notes',
      btnCreateTicket: 'Create ticket and send SMS',
      btnSaving: 'Saving...',

      // Ticket Details
      backBtn: 'Back',
      lblAssignedTo: 'Assigned to',
      lblPickupPin: 'Pickup PIN',
      secActions: 'Actions',
      btnAssignValet: 'Assign valet',
      btnStartRetrieval: 'Start retrieval',
      btnMarkReady: 'Mark ready',
      btnConfirmDelivery: 'Confirm delivery',
      msgWaitingVisitor: 'Waiting for the visitor to request the vehicle.',
      msgTicketComplete: 'This valet ticket is complete.',
      secVisitorLink: 'Visitor link',
      promptValetName: 'Enter valet name',
      secTimeline: 'Timestamps & Timeline',
      lblCheckedInAt: 'Checked in at',
      lblRequestedAt: 'Requested at',
      lblReadyAt: 'Ready at',
      lblDeliveredAt: 'Delivered at',

      // Login
      loginTitle: 'Valet',
      loginSub: 'Staff sign in',
      lblUsername: 'Username',
      lblPassword: 'Password',
      btnSignIn: 'Sign in',
      errCreds: 'Enter username and password',
      demoText: 'Demo: admin / admin123'
    },
    ar: {
      brandTitle: 'ف',
      brandSubtitle: 'فاليه',
      dashboardNav: 'لوحة التحكم',
      checkInNav: 'تسجيل دخول مركبة',
      signOut: 'تسجيل الخروج',

      // Dashboard
      dashTitle: 'لوحة التحكم المباشرة',
      dashSubtitle: 'إدارة وصول المركبات وطلبات الاستلام',
      dashCheckInBtn: '+ تسجيل دخول مركبة',
      statActive: 'نشط',
      statParked: 'مركونة',
      statRequested: 'مطلوبة',
      statRetrieving: 'جاري الإحضار',
      statReady: 'جاهزة',
      queueTitle: 'قائمة المركبات',
      refreshBtn: 'تحديث',
      thTicket: 'التذكرة',
      thVehicle: 'المركبة',
      thPlate: 'اللوحة',
      thLocation: 'الموقع',
      thStatus: 'الحالة',
      openBtn: 'فتح',
      noVehicles: 'لا توجد مركبات حتى الآن.',

      // Check-in
      checkInTitle: 'تسجيل دخول مركبة',
      checkInSub: 'إنشاء تذكرة فاليه رقمية',
      secVisitor: 'بيانات الزائر والمركبة',
      lblPhone: 'رقم هاتف الزائر',
      lblPlate: 'رقم اللوحة',
      lblMake: 'الماركة',
      lblModel: 'الموديل',
      lblColor: 'اللون',
      secParking: 'تفاصيل الإيقاف',
      lblParkingLoc: 'موقع الإيقاف',
      lblKeyLoc: 'موقع المفتاح',
      lblNotes: 'ملاحظات',
      btnCreateTicket: 'إنشاء التذكرة وإرسال SMS',
      btnSaving: 'جاري الحفظ...',

      // Ticket Details
      backBtn: 'رجوع',
      lblAssignedTo: 'المكلف بالإحضار',
      lblPickupPin: 'رمز الاستلام',
      secActions: 'الإجراءات',
      btnAssignValet: 'تكليف فاليه',
      btnStartRetrieval: 'بدء إحضار المركبة',
      btnMarkReady: 'تحديد كـ جاهزة',
      btnConfirmDelivery: 'تأكيد التسليم',
      msgWaitingVisitor: 'في انتظار طلب الزائر للمركبة.',
      msgTicketComplete: 'اكتملت هذه التذكرة.',
      secVisitorLink: 'رابط الزائر',
      promptValetName: 'أدخل اسم الفاليه',
      secTimeline: 'الجدول الزمني للأوقات',
      lblCheckedInAt: 'وقت تسجيل الدخول',
      lblRequestedAt: 'وقت طلب المركبة',
      lblReadyAt: 'وقت جاهزية المركبة',
      lblDeliveredAt: 'وقت تسليم المركبة',

      // Login
      loginTitle: 'فاليه',
      loginSub: 'تسجيل دخول الموظفين',
      lblUsername: 'اسم المستخدم',
      lblPassword: 'كلمة المرور',
      btnSignIn: 'تسجيل الدخول',
      errCreds: 'الرجاء إدخال اسم المستخدم وكلمة المرور',
      demoText: 'تجريبي: admin / admin123'
    }
  };

  t<K extends keyof typeof this.dict.en>(key: K): string {
    return this.dict[this.lang()][key] || this.dict.en[key];
  }
}

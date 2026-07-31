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
      reportsNav: 'Reports',
      usersNav: 'Users',
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
      statDelivered: 'Delivered',
      queueTitle: 'Vehicle queue',
      refreshBtn: 'Refresh',
      lblPrevious: 'Previous',
      lblNext: 'Next',
      lblPage: 'Page',
      lblOf: 'of',
      thTicket: 'Ticket',
      thVehicle: 'Vehicle',
      thPlate: 'Plate',
      thLocation: 'Location',
      thStatus: 'Status',
      openBtn: 'Open',
      noVehicles: 'No vehicles yet.',
      promptEnableNotifications: 'Enable push notifications to receive instant vehicle retrieval alerts on your device.',
      btnEnablePush: '🔔 Enable Push Notifications',
      msgPushEnabled: 'Notifications Active',

      // Check-in
      checkInTitle: 'Vehicle check-in',
      checkInSub: 'Create a digital valet ticket',
      secVisitor: 'Visitor and vehicle',
      lblPhone: 'Visitor phone',
      lblEmail: 'Visitor email',
      lblPlate: 'Plate number',
      lblMake: 'Make',
      lblModel: 'Model',
      lblColor: 'Color',
      secParking: 'Parking details',
      lblParkingLoc: 'Parking location',
      lblKeyLoc: 'Key location',
      lblNotes: 'Notes',
      hintPhoneFormat: 'Phone must be exactly 9 digits (e.g. 790824434)',
      hintEmailFormat: 'Please enter a valid email address',
      msgTicketSuccess: 'Vehicle ticket created and SMS sent successfully!',
      msgTicketError: 'Something went wrong while saving ticket. Please try again.',
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
      modalAssignTitle: 'Assign valet employee',
      modalValetNameLabel: 'Valet employee name',
      modalValetNamePlaceholder: 'e.g. Valet 1',
      btnCancel: 'Cancel',
      btnConfirmAssign: 'Assign',

      // Login
      loginTitle: 'Valet',
      loginSub: 'Staff sign in',
      lblUsername: 'Username',
      lblPassword: 'Password',
      btnSignIn: 'Sign in',
      errCreds: 'Enter username and password',
      errBadCreds: 'Invalid username or password',
      errAccountInactive: 'Your account is not active, please contact support.',
      demoText: 'Demo: admin / admin123',

      // Clients Management
      clientsNav: 'Clients',
      clientsTitle: 'Client Management',
      clientsSubtitle: 'Manage tenant accounts, activation status, and details',
      btnAddClient: '+ Add new client',
      thClientName: 'Client name',
      thContact: 'Contact',
      thActions: 'Actions',
      lblActive: 'Active',
      lblInactive: 'Inactive',
      btnEdit: 'Edit',
      btnActivate: 'Activate',
      btnDeactivate: 'Deactivate',
      modalAddClientTitle: 'Add New Client',
      modalEditClientTitle: 'Edit Client',
      lblClientName: 'Client Name',
      lblClientPhone: 'Phone Number',
      lblClientEmail: 'Email Address',
      lblClientLocation: 'Location / Address',
      lblDefaultUsername: 'Staff Account Username',
      lblDefaultPassword: 'Staff Account Password',
      lblIsActive: 'Active Account',
      btnSave: 'Save',

      // Reports & Users (Manager)
      reportsTitle: 'Reports & Analytics',
      reportsSubtitle: 'Vehicle delivery statistics and time interval reports',
      usersTitle: 'Staff Users',
      usersSubtitle: 'Manage valet staff accounts for your hotel tenant',
      lblDaily: 'Daily View',
      lblMonthly: 'Monthly View',
      lblFromDate: 'From Date',
      lblToDate: 'To Date',
      lblTotalDelivered: 'Delivered Cars',
      lblTrendChart: 'Delivery Volume Trend',
      btnAddUser: '+ Add Staff User',
      lblEditUser: 'Edit',
      modalAddUserTitle: 'Add Staff Member',
      modalEditUserTitle: 'Edit Staff User',
      modalChangePasswordTitle: 'Change Staff Password',
      lblCurrentPassword: 'Current Password',
      lblNewPassword: 'New Password',
      errCurrentPasswordIncorrect: 'Current password is incorrect.',
      btnChangePassword: 'Change Password',
      toastUserSaved: 'User account saved successfully',
      toastPasswordUpdated: 'Password updated successfully',
      lblRole: 'Role',
      errPasswordStrength: 'Password must be at least 8 characters long and contain at least one number and one special character.'
    },
    ar: {
      brandTitle: 'ف',
      brandSubtitle: 'فاليه',
      dashboardNav: 'لوحة التحكم',
      checkInNav: 'تسجيل دخول مركبة',
      reportsNav: 'التقارير',
      usersNav: 'المستخدمين',
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
      statDelivered: 'تم التسليم',
      queueTitle: 'قائمة المركبات',
      refreshBtn: 'تحديث',
      lblPrevious: 'السابق',
      lblNext: 'التالي',
      lblPage: 'صفحة',
      lblOf: 'من',
      thTicket: 'التذكرة',
      thVehicle: 'المركبة',
      thPlate: 'اللوحة',
      thLocation: 'الموقع',
      thStatus: 'الحالة',
      openBtn: 'فتح',
      noVehicles: 'لا توجد مركبات حتى الآن.',
      promptEnableNotifications: 'قم بتفعيل الإشعارات ليصلك تنبيه مباشر فور طلب إحضار أي مركبة.',
      btnEnablePush: '🔔 تفعيل الإشعارات',
      msgPushEnabled: 'الإشعارات مفعلة',

      // Check-in
      checkInTitle: 'تسجيل دخول مركبة',
      checkInSub: 'إنشاء تذكرة فاليه رقمية',
      secVisitor: 'بيانات الزائر والمركبة',
      lblPhone: 'رقم هاتف الزائر',
      lblEmail: 'البريد الإلكتروني للزائر',
      lblPlate: 'رقم اللوحة',
      lblMake: 'الماركة',
      lblModel: 'الموديل',
      lblColor: 'اللون',
      secParking: 'تفاصيل الإيقاف',
      lblParkingLoc: 'موقع الإيقاف',
      lblKeyLoc: 'موقع المفتاح',
      lblNotes: 'ملاحظات',
      hintPhoneFormat: 'رقم الهاتف يجب أن يتكون من 9 أرقام (مثال: 790824434)',
      hintEmailFormat: 'الرجاء إدخال بريد إلكتروني صحيح',
      msgTicketSuccess: 'تم إنشاء تذكرة المركبة وإرسال الرسالة بنجاح!',
      msgTicketError: 'حدث خطأ أثناء حفظ التذكرة. يرجى المحاولة مرة أخرى.',
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
      modalAssignTitle: 'تكليف موظف الفاليه',
      modalValetNameLabel: 'اسم موظف الفاليه',
      modalValetNamePlaceholder: 'مثال: فاليه 1',
      btnCancel: 'إلغاء',
      btnConfirmAssign: 'تكليف',

      // Login
      loginTitle: 'فاليه',
      loginSub: 'تسجيل دخول الموظفين',
      lblUsername: 'اسم المستخدم',
      lblPassword: 'كلمة المرور',
      btnSignIn: 'تسجيل الدخول',
      errCreds: 'الرجاء إدخال اسم المستخدم وكلمة المرور',
      errBadCreds: 'اسم المستخدم أو كلمة المرور غير صحيحة',
      errAccountInactive: 'حسابك غير نشط، يرجى التواصل مع الدعم الفني.',
      demoText: 'تجريبي: admin / admin123',

      // Clients Management
      clientsNav: 'العملاء',
      clientsTitle: 'إدارة العملاء',
      clientsSubtitle: 'إدارة حسابات الفنادق والعملاء وحالة التفعيل',
      btnAddClient: '+ إضافة عميل جديد',
      thClientName: 'اسم العميل',
      thContact: 'التواصل',
      thActions: 'الإجراءات',
      lblActive: 'نشط',
      lblInactive: 'معطل',
      btnEdit: 'تعديل',
      btnActivate: 'تفعيل',
      btnDeactivate: 'تعطيل',
      modalAddClientTitle: 'إضافة عميل جديد',
      modalEditClientTitle: 'تعديل بيانات العميل',
      lblClientName: 'اسم العميل',
      lblClientPhone: 'رقم الهاتف',
      lblClientEmail: 'البريد الإلكتروني',
      lblClientLocation: 'الموقع / العنوان',
      lblDefaultUsername: 'اسم المستخدم لحساب الموظف',
      lblDefaultPassword: 'كلمة المرور لحساب الموظف',
      lblIsActive: 'حساب نشط',
      btnSave: 'حفظ',

      // Reports & Users (Manager)
      reportsTitle: 'التقارير والتحليلات',
      reportsSubtitle: 'إحصائيات تسليم المركبات وتقارير الفترات الزمنية',
      usersTitle: 'موظفي الفاليه',
      usersSubtitle: 'إدارة حسابات الموظفين الخاصة بفندقك',
      lblDaily: 'عرض يومي',
      lblMonthly: 'عرض شهري',
      lblFromDate: 'من تاريخ',
      lblToDate: 'إلى تاريخ',
      lblTotalDelivered: 'السيارات المسلمة',
      lblTrendChart: 'مخطط حجم التسليم',
      btnAddUser: '+ إضافة موظف جديد',
      lblEditUser: 'تعديل',
      modalAddUserTitle: 'إضافة موظف جديد',
      modalEditUserTitle: 'تعديل بيانات الموظف',
      modalChangePasswordTitle: 'تغيير كلمة مرور الموظف',
      lblCurrentPassword: 'كلمة المرور الحالية',
      lblNewPassword: 'كلمة المرور الجديدة',
      errCurrentPasswordIncorrect: 'كلمة المرور الحالية غير صحيحة.',
      btnChangePassword: 'تغيير كلمة المرور',
      toastUserSaved: 'تم حفظ حساب الموظف بنجاح',
      toastPasswordUpdated: 'تم تحديث كلمة المرور بنجاح',
      lblRole: 'الدور',
      errPasswordStrength: 'يجب أن تتكون كلمة المرور من 8 أحرف على الأقل وتحتوي على رقم ورمز خاص.'
    }
  };

  t<K extends keyof typeof this.dict.en>(key: K): string {
    const langDict = this.dict[this.lang()] as Record<string, string>;
    return langDict[key as string] || this.dict.en[key];
  }
}

export type TicketStatus='CHECKED_IN'|'PARKED'|'REQUESTED'|'ASSIGNED'|'RETRIEVING'|'READY'|'DELIVERED'|'CANCELLED';

export enum UserRole {
  ADMIN = 'ADMIN',
  VALET = 'VALET',
  MANAGER = 'MANAGER'
}

export interface Ticket {id:number;ticketNumber:string;publicToken:string;visitorPhone:string;visitorEmail?:string;plateNumber:string;make:string;model:string;color:string;parkingLocation?:string;keyLocation?:string;assignedTo?:string;notes?:string;status:TicketStatus;checkedInAt:string;requestedAt?:string;readyAt?:string;deliveredAt?:string;pickupPin:string;}
export interface Dashboard {active:number;parked:number;requested:number;retrieving:number;ready:number;delivered:number;}
export interface CreateTicket {visitorPhone:string;visitorEmail?:string;plateNumber:string;make:string;model:string;color:string;parkingLocation:string;keyLocation:string;notes:string;}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface Client {
  id: number;
  name: string;
  phoneNumber?: string;
  email?: string;
  location?: string;
  isActive: boolean;
  createdAt: string;
}

export interface CreateClient {
  name: string;
  phoneNumber?: string;
  email?: string;
  location?: string;
  isActive?: boolean;
  username?: string;
  defaultPassword?: string;
}

export interface UpdateClient {
  name: string;
  phoneNumber?: string;
  email?: string;
  location?: string;
  isActive?: boolean;
}

export interface UserAccountResponse {
  id: number;
  username: string;
  role: UserRole;
  clientId: number;
  isActive: boolean;
}

export interface CreateUserRequest {
  username: string;
  password?: string;
  role?: UserRole;
}

export interface UpdateUserRequest {
  username: string;
  password?: string;
  role?: UserRole;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ReportDataPoint {
  label: string;
  count: number;
}

export interface ReportAnalytics {
  mode: 'daily' | 'monthly';
  fromDate: string;
  toDate: string;
  totalDelivered: number;
  totalParked: number;
  totalRequested: number;
  dataPoints: ReportDataPoint[];
}
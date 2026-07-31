import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Dashboard, CreateTicket, Ticket, TicketStatus, Client, CreateClient, UpdateClient, UserAccountResponse, CreateUserRequest, UpdateUserRequest, ChangePasswordRequest, ReportAnalytics, PageResponse } from './models';

@Injectable({ providedIn: 'root' })
export class ValetApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '';

  // Tickets
  list(page: number = 0, size: number = 15, status?: string) {
    let params = `?page=${page}&size=${size}`;
    if (status && status !== 'ALL') params += `&status=${status}`;
    return this.http.get<PageResponse<Ticket>>(`${this.base}/api/tickets${params}`);
  }
  dashboard() { return this.http.get<Dashboard>(`${this.base}/api/tickets/dashboard`); }
  create(r: CreateTicket) { return this.http.post<Ticket>(`${this.base}/api/tickets`, r); }
  get(id: number) { return this.http.get<Ticket>(`${this.base}/api/tickets/${id}`); }
  assign(id: number, assignedTo: string) { return this.http.post<Ticket>(`${this.base}/api/tickets/${id}/assign`, { assignedTo }); }
  transition(id: number, status: TicketStatus) { return this.http.post<Ticket>(`${this.base}/api/tickets/${id}/status/${status}`, {}); }
  publicGet(token: string) { return this.http.get<Ticket>(`${this.base}/public/tickets/${token}`); }
  request(token: string) { return this.http.post<Ticket>(`${this.base}/public/tickets/${token}/request`, {}); }

  // Clients (Admin Only)
  getClients() { return this.http.get<Client[]>(`${this.base}/api/clients`); }
  getClient(id: number) { return this.http.get<Client>(`${this.base}/api/clients/${id}`); }
  createClient(data: CreateClient) { return this.http.post<Client>(`${this.base}/api/clients`, data); }
  updateClient(id: number, data: UpdateClient) { return this.http.put<Client>(`${this.base}/api/clients/${id}`, data); }

  // Users (Manager / Admin)
  getUsers() { return this.http.get<UserAccountResponse[]>(`${this.base}/api/users`); }
  createUser(r: CreateUserRequest) { return this.http.post<UserAccountResponse>(`${this.base}/api/users`, r); }
  updateUser(id: number, r: UpdateUserRequest) { return this.http.put<UserAccountResponse>(`${this.base}/api/users/${id}`, r); }
  changeUserPassword(id: number, r: ChangePasswordRequest) { return this.http.put<UserAccountResponse>(`${this.base}/api/users/${id}/change-password`, r); }
  toggleUserActive(id: number) { return this.http.put<UserAccountResponse>(`${this.base}/api/users/${id}/toggle-active`, {}); }

  // Reports (Manager / Admin)
  getReportAnalytics(mode: string, from?: string, to?: string) {
    let params = `?mode=${mode}`;
    if (from) params += `&from=${from}`;
    if (to) params += `&to=${to}`;
    return this.http.get<ReportAnalytics>(`${this.base}/api/reports/analytics${params}`);
  }
}
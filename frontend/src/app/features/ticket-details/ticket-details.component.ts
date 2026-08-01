import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Ticket, TicketStatus } from '../../core/models';
import { ValetApiService } from '../../core/valet-api.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { LanguageService } from '../../core/language.service';

@Component({
  standalone: true,
  imports: [RouterLink, DatePipe, FormsModule, PageHeaderComponent, StatusBadgeComponent],
  templateUrl: './ticket-details.component.html',
  styleUrl: './ticket-details.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketDetailsComponent {
  private readonly api = inject(ValetApiService);
  private readonly id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));
  readonly langService = inject(LanguageService);
  readonly ticket = signal<Ticket | null>(null);

  readonly showAssignModal = signal(false);
  readonly loading = signal(false);
  valetName = 'Valet 1';

  constructor() {
    this.load();
  }

  load() {
    this.api.get(this.id).subscribe(t => this.ticket.set(t));
  }

  getVisitorLink(token: string): string {
    return `${window.location.origin}/v/${token}`;
  }

  openAssignModal() {
    this.showAssignModal.set(true);
  }

  closeAssignModal() {
    this.showAssignModal.set(false);
  }

  submitAssign() {
    if (this.valetName && this.valetName.trim()) {
      this.loading.set(true);
      this.api.assign(this.id, this.valetName.trim()).subscribe({
        next: (t) => {
          this.ticket.set(t);
          this.closeAssignModal();
          this.loading.set(false);
        },
        error: () => this.loading.set(false)
      });
    }
  }

  move(status: TicketStatus) {
    this.loading.set(true);
    this.api.transition(this.id, status).subscribe({
      next: (t) => {
        this.ticket.set(t);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
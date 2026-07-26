import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Ticket, TicketStatus } from '../../core/models';
import { ValetApiService } from '../../core/valet-api.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { LanguageService } from '../../core/language.service';

@Component({
  standalone: true,
  imports: [RouterLink, DatePipe, PageHeaderComponent, StatusBadgeComponent],
  templateUrl: './ticket-details.component.html',
  styleUrl: './ticket-details.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketDetailsComponent {
  private readonly api = inject(ValetApiService);
  private readonly id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));
  readonly langService = inject(LanguageService);
  readonly ticket = signal<Ticket | null>(null);

  constructor() {
    this.load();
  }

  load() {
    this.api.get(this.id).subscribe(t => this.ticket.set(t));
  }

  assign() {
    const promptText = this.langService.t('promptValetName');
    const name = prompt(promptText, 'Valet 1');
    if (name) this.api.assign(this.id, name).subscribe(t => this.ticket.set(t));
  }

  move(status: TicketStatus) {
    this.api.transition(this.id, status).subscribe(t => this.ticket.set(t));
  }
}
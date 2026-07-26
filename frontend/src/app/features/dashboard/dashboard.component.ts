import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ValetApiService } from '../../core/valet-api.service';
import { Dashboard, Ticket } from '../../core/models';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { LanguageService } from '../../core/language.service';

@Component({
  standalone: true,
  imports: [RouterLink, PageHeaderComponent, StatusBadgeComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent {
  private readonly api = inject(ValetApiService);
  private readonly destroyRef = inject(DestroyRef);
  readonly langService = inject(LanguageService);
  readonly stats = signal<Dashboard | null>(null);
  readonly tickets = signal<Ticket[]>([]);

  constructor() {
    this.load();
    const intervalId = setInterval(() => {
      const currentStats = this.stats();
      const currentTickets = this.tickets();
      const hasParked = currentStats ? currentStats.parked > 0 : currentTickets.some(t => t.status === 'PARKED');
      if (hasParked) {
        this.load();
      }
    }, 3000);

    this.destroyRef.onDestroy(() => clearInterval(intervalId));
  }

  load() {
    this.api.dashboard().subscribe(v => this.stats.set(v));
    this.api.list().subscribe(v => this.tickets.set(v));
  }
}
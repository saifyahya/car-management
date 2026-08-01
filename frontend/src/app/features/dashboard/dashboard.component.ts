import { ChangeDetectionStrategy, Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ValetApiService } from '../../core/valet-api.service';
import { Dashboard, Ticket } from '../../core/models';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { LanguageService } from '../../core/language.service';
import { WebPushService } from '../../core/web-push.service';

@Component({
  standalone: true,
  imports: [RouterLink, PageHeaderComponent, StatusBadgeComponent, DatePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent {
  private readonly api = inject(ValetApiService);
  private readonly destroyRef = inject(DestroyRef);
  readonly langService = inject(LanguageService);
  readonly pushService = inject(WebPushService);

  readonly stats = signal<Dashboard | null>(null);
  readonly tickets = signal<Ticket[]>([]);
  readonly selectedFilter = signal<string>('ALL');

  readonly currentPage = signal<number>(0);
  readonly pageSize = signal<number>(15);
  readonly totalPages = signal<number>(1);
  readonly totalElements = signal<number>(0);
  readonly isLastPage = signal<boolean>(true);

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

  setFilter(filter: string): void {
    if (this.selectedFilter() === filter) {
      this.selectedFilter.set('ALL');
    } else {
      this.selectedFilter.set(filter);
    }
    this.currentPage.set(0);
    this.load();
  }

  nextPage(): void {
    if (!this.isLastPage() && this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(p => p + 1);
      this.load();
    }
  }

  prevPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(p => p - 1);
      this.load();
    }
  }

  load() {
    this.api.dashboard().subscribe(v => this.stats.set(v));
    this.api.list(this.currentPage(), this.pageSize(), this.selectedFilter()).subscribe({
      next: res => {
        this.tickets.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.isLastPage.set(res.last);
      }
    });
  }

  enablePush(): void {
    this.pushService.initAndSubscribe();
  }
}
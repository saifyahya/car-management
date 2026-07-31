import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReportAnalytics } from '../../core/models';
import { ValetApiService } from '../../core/valet-api.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { LanguageService } from '../../core/language.service';

@Component({
  standalone: true,
  imports: [FormsModule, PageHeaderComponent],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportsComponent {
  private readonly api = inject(ValetApiService);
  readonly langService = inject(LanguageService);

  readonly mode = signal<'daily' | 'monthly'>('daily');
  readonly fromDate = signal<string>(this.getDefaultFromDate('daily'));
  readonly toDate = signal<string>(this.getDefaultToDate('daily'));

  readonly analytics = signal<ReportAnalytics | null>(null);
  readonly loading = signal(true);

  constructor() {
    this.load();
  }

  getDefaultFromDate(m: 'daily' | 'monthly'): string {
    const d = new Date();
    if (m === 'monthly') {
      const year = d.getFullYear();
      return `${year}-01`; // Start of current year (January)
    } else {
      const day = d.getDay();
      const diff = d.getDate() - day + (day === 0 ? -6 : 1); // Monday
      const monday = new Date(d.setDate(diff));
      return monday.toISOString().split('T')[0];
    }
  }

  getDefaultToDate(m: 'daily' | 'monthly'): string {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    if (m === 'monthly') {
      return `${year}-${month}`; // Current month
    } else {
      return d.toISOString().split('T')[0];
    }
  }

  setMode(m: 'daily' | 'monthly') {
    if (this.mode() === m) return;
    this.mode.set(m);
    this.fromDate.set(this.getDefaultFromDate(m));
    this.toDate.set(this.getDefaultToDate(m));
    this.load();
  }

  onDateChange() {
    this.load();
  }

  load() {
    this.loading.set(true);
    this.api.getReportAnalytics(this.mode(), this.fromDate(), this.toDate()).subscribe({
      next: data => {
        this.analytics.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  getMaxCount(): number {
    const data = this.analytics()?.dataPoints || [];
    if (data.length === 0) return 10;
    const max = Math.max(...data.map(d => d.count));
    return max > 0 ? max : 10;
  }
}

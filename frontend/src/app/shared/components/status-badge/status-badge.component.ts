import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { TicketStatus } from '../../../core/models';
import { LanguageService } from '../../../core/language.service';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatusBadgeComponent {
  readonly status = input.required<TicketStatus>();
  readonly customLabel = input<string>();
  readonly langService = inject(LanguageService);
}
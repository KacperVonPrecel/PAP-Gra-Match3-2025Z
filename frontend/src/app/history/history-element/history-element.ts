import { Component, Input } from '@angular/core';
import { HistoryMatchData } from '../history-service';

@Component({
	selector: 'app-history-element',
	imports: [],
	templateUrl: './history-element.html',
	styleUrl: './history-element.scss'
})
export class HistoryElement {
	@Input({ required: true }) historyMatchData!: HistoryMatchData;
	@Input({ required: true }) now!: number;
  
	msToHumanReadable(short = false) {
		const seconds = Math.floor((this.now - Number(this.historyMatchData.finishTime)) / 1000);
		const minutes = Math.floor(seconds / 60);
		const hours = Math.floor(minutes / 60);
		const days = Math.floor(hours / 24);
		const weeks = Math.floor(days / 7);
		const months = Math.floor(days / 30);
		const years = Math.floor(days / 365);

		if (years > 0) {
			return short ? `${years}y` : `${years} year${years > 1 ? 's' : ''}`;
		}
		if (months > 0) {
			return short ? `${months}mo` : `${months} month${months > 1 ? 's' : ''}`;
		}
		if (weeks > 0) {
			return short ? `${weeks}w` : `${weeks} week${weeks > 1 ? 's' : ''}`;
		}
		if (days > 0) {
			return short ? `${days}d` : `${days} day${days > 1 ? 's' : ''}`;
		}
		if (hours > 0) {
			return short ? `${hours}h` : `${hours} hour${hours > 1 ? 's' : ''}`;
		}
		if (minutes > 0) {
			return short ? `${minutes}m` : `${minutes} minute${minutes > 1 ? 's' : ''}`;
		}
		if (seconds > 0) {
			return short ? `${seconds}s` : `${seconds} second${seconds > 1 ? 's' : ''}`;
		}

		return 'A moment ago';
	}
}

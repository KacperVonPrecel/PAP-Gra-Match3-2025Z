import { Component, signal } from '@angular/core';
import { Match3Service, MoveRequest } from '../match3-service';
import { BoardState, GameState } from '../game-state';
import { GameBoard } from './board/game-board';

@Component({
	selector: 'app-match3',
	imports: [GameBoard],
	templateUrl: './match3.html',
	styleUrl: './match3.scss'
})
export class Match3 {
	private gameId?: number = 0;
	private gameState?: GameState; //nullable because if we dont connect, no state
	public moveValid = signal<boolean | null>(null);
	playerId = 0;

	constructor(private socket: Match3Service) {}

	ngOnInit(): void {
		this.socket.client.onConnect = () => {
			console.log('STOMP connected');
			this.connect(0);
		};
	}

	get isItMyTurn(): boolean {
		if (!this.gameState) {
			return false;
		}
		if (this.gameState!.currentPlayerId == this.playerId) {
			return true;
		}
		return false;
	}

	get boardState(): BoardState | null {
		if (this.gameState) {
			return this.gameState.boardState;
		}
		return null;
	}

	connect(gameId: number): void {
		console.log(gameId);
		this.gameId = gameId;
		this.socket.subscribeToGame(this.gameId);
		this.socket.gameState$.subscribe((gameState) => {
			console.log(gameState);
			if (gameState) {
				this.moveValid.set(true);
				this.gameState = gameState;
			} else {
				this.moveValid.set(false);
			}
		});
		this.fetchState();
	}

	disconnect(): void {
		if (this.gameId != undefined) {
			this.socket.unsubscribeFromGame();
			this.gameId = undefined;
		}
	}

	fetchState(): void {
		if (this.gameId != undefined) this.socket?.fetchState(this.gameId);
	}

	makeMove(move: MoveRequest): void {
		console.log('Make move in match3 executed');
		if (this.gameId != undefined) {
			this.socket.makeMove(this.gameId, move);
		}
	}
}

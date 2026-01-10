import { Component, signal } from '@angular/core';
import { Board, Match3Service, MoveRequest } from '../match3-service';
import { BoardState, GameState } from '../game-state';
import { GameBoard } from './board/game-board';

@Component({
	selector: 'app-match3',
	imports: [GameBoard],
	templateUrl: './match3.html',
	styleUrl: './match3.scss'
})
export class Match3 {
	private gameId?: number;
	private gameState?: GameState; //nullable because if we dont connect, no state
	public moveValid = signal<boolean | null>(null);
	playerId = 0;

	constructor(private socket: Match3Service) {}

	ngOnInit(): void {
		//here getting starting game state
		this.gameState = this.socket.getCurrentGameState();
	}

	get isItMyTurn(): boolean {
		if (!this.gameState) {
			return false;
		}
		if (this.gameState!.currentTurnId == this.playerId) {
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
		this.socket.board$.subscribe((board) => {
			//this.updateBoard(board);
		});
		//this.fetchBoard();
	}

	disconnect(): void {
		if (this.gameId != undefined) {
			this.socket.unsubscribeFromGame();
			this.gameId = undefined;
		}
	}

	makeMove(move: MoveRequest): void {
		this.socket.sendMoveRequest(move).subscribe((newState) => {
			if (newState === null) {
				this.moveValid.set(false);
				return;
			}
			this.moveValid.set(true);
			this.gameState = newState;
		});
	}
}

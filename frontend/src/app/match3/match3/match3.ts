import { Component, signal } from '@angular/core';
import { Match3Service, MoveRequest } from '../match3-service';
import { BoardState, GameStartData, GameState, PlayerData, PlayerState } from '../game-state';
import { GameBoard } from './board/game-board';
import { CharactersDisplay } from './characters-display/characters-display';

@Component({
	selector: 'app-match3',
	imports: [GameBoard, CharactersDisplay],
	templateUrl: './match3.html',
	styleUrl: './match3.scss'
})
export class Match3 {
	private gameId?: number = 0;
	private gameStartData?: GameStartData;
	private gameState?: GameState; //nullable because if we dont connect, no state
	public _moveValid: { valid: boolean | null; moveId: number } = { valid: null, moveId: 0 };
	playerId: number = 0;
	lastSentMoveRequestId: number = 0;
	lastProcessedMoveId: number = 0;

	constructor(private socket: Match3Service) {}

	ngOnInit(): void {
		this.socket.client.onConnect = () => {
			console.log('STOMP connected');
			this.connect(0);
		};
	}

	get moveValid(): { valid: boolean | null; moveId: number } {
		return this._moveValid;
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

	get playerStates(): Map<number, PlayerState> | null {
		if (this.gameState) {
			return this.gameState.playerStates;
		}
		return null;
	}

	get playersData(): Map<number, PlayerData> | null {
		if (this.gameStartData) {
			return this.gameStartData.playerData;
		}
		return null;
	}

	get myData() {
		const playersData = this.playersData;
		if (!this.playersData) return null;
		for (const [id, data] of playersData!) {
			if (id === this.playerId) {
				return data;
			}
		}
		return null;
	}

	get opponentData(): PlayerData | null {
		const playersData = this.playersData;
		if (!this.playersData) return null;
		for (const [id, data] of playersData!) {
			if (id != this.playerId) {
				return data;
			}
		}
		return null;
	}

	get opponentState(): PlayerState | null {
		const playerStates = this.playerStates;
		if (!playerStates) return null;
		for (const [id, state] of playerStates!) {
			if (id != this.playerId) {
				return state;
			}
		}
		return null;
	}

	get myState(): PlayerState | null {
		const playerStates = this.playerStates;
		if (!playerStates) return null;
		for (const [id, state] of playerStates!) {
			if (id === this.playerId) {
				return state;
			}
		}
		return null;
	}

	connect(gameId: number): void {
		console.log(gameId);
		this.gameId = gameId;
		this.socket.subscribeToGame(this.gameId);
		this.socket.gameState$.subscribe((gameState) => {
			//if processed id is less than sent move id -> this means make move set a new last sent move id, the user sent a move
			//(unless a refresh happened exactly after a user moved, but before the server responded? depending on how the backend handles that -> but even then it will work fine,
			// since refresh has no animations to display)
			//if processed id is equal to sent move -> the new state is a result of opponents move or refresh
			if (this.lastProcessedMoveId < this.lastSentMoveRequestId) {
				console.log('my valid move', this.lastProcessedMoveId, this.lastSentMoveRequestId);
				this.lastProcessedMoveId = this.lastSentMoveRequestId; //setting my move as the last processedId
				if (gameState) {
					this._moveValid = { valid: true, moveId: this.lastSentMoveRequestId };
					this.gameState = gameState;
				} else {
					this._moveValid = { valid: false, moveId: this.lastSentMoveRequestId };
				}
			} else {
				console.log('NOT my move', this.lastSentMoveRequestId, this.lastProcessedMoveId);
				//this was the opponents move or page refresh
				if (gameState) {
					this.gameState = gameState;
				}
				this._moveValid = { valid: null, moveId: this.lastSentMoveRequestId };
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

	makeMove(swapAttempt: { move: MoveRequest; moveId: number }): void {
		console.log('Make move in match3 executed');
		if (this.gameId != undefined) {
			this.lastSentMoveRequestId = swapAttempt.moveId;
			this.socket.makeMove(this.gameId, swapAttempt.move);
		}
	}
}

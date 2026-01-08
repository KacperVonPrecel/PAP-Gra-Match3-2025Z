import { Component } from '@angular/core';
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
	public board: String = '';

	constructor(private socket: Match3Service) {}

	ngOnInit(): void {
		this.gameState = this.socket.getMockInitialState();
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
			this.updateBoard(board);
		});
		this.fetchBoard();
	}

	disconnect(): void {
		if (this.gameId != undefined) {
			this.socket.unsubscribeFromGame();
			this.gameId = undefined;
		}
	}

	fetchBoard(): void {
		if (this.gameId != undefined) this.socket?.updateBoard(this.gameId);
	}

	updateBoard(board: Board): void {
		const boardBlocks = board.board;

		let output = '';
		for (let i = 0; i < boardBlocks.length; i++) {
			for (let j = 0; j < boardBlocks[i].length; j++) {
				output += boardBlocks[i][j].blockType.toString();
			}
			output += '\n';
		}
		this.board = output;
	}

	fillBoard(): void {
		if (this.gameId != undefined) this.socket.fillBoard(this.gameId);
	}

	dropFloatingBlocks(): void {
		if (this.gameId != undefined) this.socket.dropFloatingBlocks(this.gameId);
	}

	makeMove(sourceRow: number, sourceColumn: number, targetRow: number, targetColumn: number): void {
		//this.socket.swapBlocks(this.gameId, moveRequest);
	}

	destroyMatchedBlocks(): void {
		if (this.gameId != undefined) this.socket.destroyMatchedBlocks(this.gameId);
	}
}

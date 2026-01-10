import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable, of, Subject } from 'rxjs';
import SockJS from 'sockjs-client';
import { GameState, Player, Crystal, Position } from './game-state';

@Injectable({
	providedIn: 'root'
})
export class Match3Service {
	public client: Client;
	private subscription?: StompSubscription;

	public board$ = new Subject<Board>();

	constructor() {
		this.client = new Client({
			webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
			reconnectDelay: 5000
		});
		this.client.onConnect = () => {
			console.log('STOMP connected');
		};

		this.client.activate();
	}

	subscribeToGame(gameId: number): void {
		if (this.subscription != undefined) {
			this.unsubscribeFromGame();
		}

		this.subscription = this.client.subscribe(`/topic/board/${gameId}/state`, (msg: IMessage) => {
			const board: Board = { board: JSON.parse(msg.body) };
			this.board$.next(board);
		});

		console.log(`Subscribed to game with id: ${gameId}`);
	}

	unsubscribeFromGame(): void {
		this.subscription?.unsubscribe();
		this.subscription = undefined;

		console.log('Unsubscribed from game');
	}

	updateBoard(gameId: number): void {
		this.client.publish({ destination: `/app/board/${gameId}/state`, body: '{}' });
		console.log('board updated');
	}

	fillBoard(gameId: number): void {
		this.client.publish({ destination: `/app/board/${gameId}/fillBoard`, body: '{}' });
	}

	dropFloatingBlocks(gameId: number): void {
		this.client.publish({ destination: `/app/board/${gameId}/dropFloatingBlocks`, body: '{}' });
	}

	// TODO: Handle swap success status
	swapBlocks(gameId: number, moveRequest: MoveRequest): void {
		this.client.publish({
			destination: `/app/board/${gameId}/swap`,
			body: JSON.stringify(moveRequest)
		});
	}

	destroyMatchedBlocks(gameId: number): void {
		this.client.publish({ destination: `/app/board/${gameId}/destroyMatchedBlocks`, body: `{}` });
	}

	sendMoveRequest(move: MoveRequest): Observable<GameState | null> {
		const mockBoard: Crystal[][] = [
			[
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.CITRINE }
			],
			[
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.DIAMOND }
			],
			[
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.HEMATITE }
			],
			[
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.AMETHYST }
			],
			[
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.CITRINE }
			],
			[
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.RUBY }
			],
			[
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.DIAMOND }
			],
			[
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.EMERALD }
			]
		];
		const mockState: GameState = {
			currentPlayer: Player.ME,
			boardState: {
				board: mockBoard,
				allowedMoves: [],
				canPlayerMove: true,
				animationSteps: [
					{
						destroyed: [
							{ row: 4, column: 0 },
							{ row: 4, column: 1 },
							{ row: 4, column: 2 }
						],
						swapped: null,
						falling: [{ source: { row: 0, column: 6 }, target: { row: 3, column: 6 } }],
						newBlocks: [],
						resetBoard: false
					}
				]
			}
		};
		return of(mockState).pipe();
	}

	getCurrentGameState(): GameState {
		const mockBoard: Crystal[][] = [
			[
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.DISABLED }
			],
			[
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.EMPTY },
				{ crystalType: CrystalType.DIAMOND }
			],
			[
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.EMPTY },
				{ crystalType: CrystalType.HEMATITE }
			],
			[
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.EMPTY },
				{ crystalType: CrystalType.AMETHYST }
			],
			[
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.CITRINE }
			],
			[
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.RUBY }
			],
			[
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.DIAMOND }
			],
			[
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.EMERALD },
				{ crystalType: CrystalType.DIAMOND },
				{ crystalType: CrystalType.CITRINE },
				{ crystalType: CrystalType.AMETHYST },
				{ crystalType: CrystalType.HEMATITE },
				{ crystalType: CrystalType.RUBY },
				{ crystalType: CrystalType.EMERALD }
			]
		];

		const allowedMoves: MoveRequest[] = [
			{ source: { row: 0, column: 0 }, target: { row: 0, column: 1 } },
			{ source: { row: 5, column: 1 }, target: { row: 6, column: 1 } },
			{ source: { row: 4, column: 2 }, target: { row: 4, column: 3 } }
		];
		return {
			currentPlayer: Player.ME,
			boardState: {
				board: mockBoard,
				allowedMoves: allowedMoves,
				animationSteps: [],
				canPlayerMove: true
			}
		};
	}
}

export interface Block {
	blockType: number;
}

export interface Board {
	board: Block[][];
}

export interface MoveRequest {
	source: Position;
	target: Position;
}
export enum CrystalType {
	AMETHYST = 'AMETHYST',
	CITRINE = 'CITRINE',
	DIAMOND = 'DIAMOND',
	EMERALD = 'EMERALD',
	HEMATITE = 'HEMATITE',
	RUBY = 'RUBY',
	DISABLED = 'DISABLED',
	EMPTY = 'EMPTY'
}

export function getCrystalFileName(CrystalType: CrystalType): string {
	return 'assets/crystals/' + crystalFileMap[CrystalType];
}

const crystalFileMap: { [key in CrystalType]: string } = {
	[CrystalType.AMETHYST]: 'amethyst.svg',
	[CrystalType.CITRINE]: 'citrine.svg',
	[CrystalType.DIAMOND]: 'diamond.svg',
	[CrystalType.EMERALD]: 'emerald.svg',
	[CrystalType.HEMATITE]: 'hematite.svg',
	[CrystalType.RUBY]: 'ruby.svg',
	[CrystalType.DISABLED]: 'ruby.svg',
	[CrystalType.EMPTY]: 'ruby.svg'
};

import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import SockJS from 'sockjs-client';
import { GameState, Player, Crystal, CrystalState } from './game-state';

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

	getMockInitialState(): GameState {
		const mockBoard: Crystal[][] = [
			[
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.HEMATITE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE }
			],
			[
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.HEMATITE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE }
			],
			[
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.HEMATITE, state: CrystalState.IDLE }
			],
			[
				{ crystalType: CrystalType.HEMATITE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE }
			],
			[
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.HEMATITE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE }
			],
			[
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE },
				{ crystalType: CrystalType.HEMATITE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE }
			],
			[
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.HEMATITE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE }
			],
			[
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE },
				{ crystalType: CrystalType.DIAMOND, state: CrystalState.IDLE },
				{ crystalType: CrystalType.CITRINE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.AMETHYST, state: CrystalState.IDLE },
				{ crystalType: CrystalType.HEMATITE, state: CrystalState.IDLE },
				{ crystalType: CrystalType.RUBY, state: CrystalState.IDLE },
				{ crystalType: CrystalType.EMERALD, state: CrystalState.IDLE }
			]
		];
		const allowedMoves: MoveRequest[] = [
			{ sourceRow: 3, sourceColumn: 3, targetRow: 3, targetColumn: 4 },
			{ sourceRow: 5, sourceColumn: 1, targetRow: 6, targetColumn: 1 }
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
	sourceRow: number;
	sourceColumn: number;
	targetRow: number;
	targetColumn: number;
}
export enum CrystalType {
	AMETHYST = 'AMETHYST',
	CITRINE = 'CITRINE',
	DIAMOND = 'DIAMOND',
	EMERALD = 'EMERALD',
	HEMATITE = 'HEMATITE',
	RUBY = 'RUBY'
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
	[CrystalType.RUBY]: 'ruby.svg'
};

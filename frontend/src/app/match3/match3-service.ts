import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable, of, Subject } from 'rxjs';
import SockJS from 'sockjs-client';
import { GameState, Position } from './game-state';

@Injectable({
	providedIn: 'root'
})
export class Match3Service {
	public client: Client;
	private subscription?: StompSubscription;

	public gameState$ = new Subject<GameState>();

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
			const gamestate: GameState = JSON.parse(msg.body);
			this.gameState$.next(gamestate);
		});

		console.log(`Subscribed to game with id: ${gameId}`);
	}

	unsubscribeFromGame(): void {
		this.subscription?.unsubscribe();
		this.subscription = undefined;

		console.log('Unsubscribed from game');
	}

	fetchState(gameId: number): void {
		this.client.publish({ destination: `/app/board/${gameId}/getState`, body: '{}' });
	}

	makeMove(gameId: number, moveRequest: MoveRequest): void {
		this.client.publish({
			destination: `/app/board/${gameId}/playTurn`,
			body: JSON.stringify(moveRequest)
		});
	}
}

export interface MoveRequest {
	source: Position;
	target: Position;
}

export enum BlockType {
	AMETHYST = 'AMETHYST',
	CITRINE = 'CITRINE',
	DIAMOND = 'DIAMOND',
	EMERALD = 'EMERALD',
	HEMATITE = 'HEMATITE',
	RUBY = 'RUBY',
	DISABLED = 'DISABLED',
	EMPTY = 'EMPTY'
}

export function getBlockFileName(blockType: BlockType): string {
	return 'assets/crystals/' + blockFileMap[blockType];
}

const blockFileMap: { [key in BlockType]: string } = {
	[BlockType.AMETHYST]: 'amethyst.svg',
	[BlockType.CITRINE]: 'citrine.svg',
	[BlockType.DIAMOND]: 'diamond.svg',
	[BlockType.EMERALD]: 'emerald.svg',
	[BlockType.HEMATITE]: 'hematite.svg',
	[BlockType.RUBY]: 'ruby.svg',
	[BlockType.DISABLED]: 'ruby.svg',
	[BlockType.EMPTY]: 'ruby.svg'
};

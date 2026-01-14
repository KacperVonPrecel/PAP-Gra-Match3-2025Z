import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable, of, Subject } from 'rxjs';
import SockJS from 'sockjs-client';
import { BoardState, GameCharacter, GameState, Match3Block, PlayerData, PlayerState, Position } from './game-state';
import { CharacterType } from '../user-data/user-data-service';

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

	getMockState(): GameState {
		const board: Match3Block[][] = [
			[{ blockType: BlockType.DIAMOND }, { blockType: BlockType.RUBY }],
			[{ blockType: BlockType.RUBY }, { blockType: BlockType.RUBY }]
		];
		const boardState: BoardState = {
			board,
			allowedMoves: [{ source: { row: 1, column: 0 }, target: { row: 1, column: 1 } }],
			animationSteps: []
		};
		const playerState = new Map<number, number>([
			[0, 50],
			[1, 80],
			[2, 100]
		]);
		const opponentState = new Map<number, number>([
			[3, 50],
			[4, 110],
			[5, 110]
		]);
		const playerStates = new Map<number, PlayerState>();
		playerStates.set(0, { charactersHealth: playerState });
		playerStates.set(1, { charactersHealth: opponentState });
		return { boardState: boardState, currentPlayerId: 0, playerStates: playerStates, attackingCharacterId: null };
	}

	getMockMove() {
		const board: Match3Block[][] = [
			[{ blockType: BlockType.EMERALD }, { blockType: BlockType.RUBY }],
			[{ blockType: BlockType.RUBY }, { blockType: BlockType.RUBY }]
		];
		const boardState: BoardState = { board, allowedMoves: [], animationSteps: [] };
		const playerState = new Map<number, number>([
			[0, 50],
			[1, 20],
			[2, 100]
		]);
		const opponentState = new Map<number, number>([
			[3, 50],
			[4, 70],
			[5, 0]
		]);
		const playerStates = new Map<number, PlayerState>();
		playerStates.set(0, { charactersHealth: playerState });
		playerStates.set(1, { charactersHealth: opponentState });
		return { boardState: boardState, currentPlayerId: 0, playerStates: playerStates, attackingCharacterId: 0 };
	}

	mockGameStartData() {
		const board: Match3Block[][] = [
			[{ blockType: BlockType.DIAMOND }, { blockType: BlockType.RUBY }],
			[{ blockType: BlockType.RUBY }, { blockType: BlockType.RUBY }]
		];
		const boardState: BoardState = { board, allowedMoves: [], animationSteps: [] };
		const playerCharacters: GameCharacter[] = [
			{ characterId: 0, characterType: CharacterType.SACRED_CAT, maxHealth: 100, damage: 100 },
			{ characterId: 1, characterType: CharacterType.AMETHYST_ENCHANTRESS, maxHealth: 100, damage: 100 },
			{ characterId: 2, characterType: CharacterType.RUBY_HORNED_DAME, maxHealth: 80, damage: 100 }
		];
		const opponentCharacters: GameCharacter[] = [
			{ characterId: 3, characterType: CharacterType.AMETHYST_ENCHANTRESS, maxHealth: 90, damage: 100 },
			{ characterId: 4, characterType: CharacterType.EMERALD_CORE_KNIGHT, maxHealth: 110, damage: 100 },
			{ characterId: 5, characterType: CharacterType.TRASH_MAN, maxHealth: 100, damage: 100 }
		];
		const playerState = new Map<number, number>([
			[1, 100],
			[2, 80]
		]);
		const opponentState = new Map<number, number>([
			[3, 90],
			[4, 100]
		]);
		const playerStates = new Map<number, PlayerState>();
		playerStates.set(0, { charactersHealth: playerState });
		playerStates.set(1, { charactersHealth: opponentState });
		const playerData = new Map<number, PlayerData>();
		playerData.set(0, { playerId: 1, playerName: 'PlayerOne', playerElo: 1500, characters: playerCharacters });
		playerData.set(1, { playerId: 2, playerName: 'Opponent', playerElo: 1480, characters: opponentCharacters });
		return { gameId: 'mock-game-123', playerData };
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

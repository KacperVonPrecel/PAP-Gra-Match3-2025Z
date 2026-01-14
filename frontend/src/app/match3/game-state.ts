import { CharacterType } from '../user-data/user-data-service';
import { BlockType, MoveRequest } from './match3-service';
export interface GameState {
	boardState: BoardState;
	currentPlayerId: number;
	playerStates: Map<number, PlayerState>;
}

export interface Match3Block {
	blockType: BlockType;
}

export interface AnimationStep {
	board: Match3Block[][];
	swapped: MoveRequest | null; //null when no swap in step
	destroyed: Position[]; //empty array will be passed to these fields if there is nothing destroyed
	falling: MoveRequest[];
	newBlocks: NewBlock[];
	resetBoard: boolean;
}

export interface Position {
	row: number;
	column: number;
}

export interface NewBlock {
	position: Position;
	block: Match3Block;
}

export interface BoardState {
	board: Match3Block[][];
	allowedMoves: MoveRequest[];
	animationSteps: AnimationStep[];
}

export interface PlayerData {
	playerId: number;
	playerName: string;
	playerElo: number;
	characters: GameCharacter[];
}

export interface PlayerState {
	charactersHealth: Map<number, number>;
}

export interface GameStartData {
	gameId: string;
	gameState: GameState;
	playerData: Map<number, PlayerData>;
}

export interface GameCharacter {
	characterId: number;
	characterType: CharacterType;
	maxHealth: number;
	damage: number;
}

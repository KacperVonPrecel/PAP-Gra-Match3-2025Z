import { CrystalType, MoveRequest } from './match3-service';
export interface GameState {
	currentPlayer: Player;
	boardState: BoardState;
	currentTurnId: number;
}

export interface Crystal {
	crystalType: CrystalType;
}

export enum Player {
	ME = 'ME',
	OPPONENT = 'OPPONENT'
}

export interface PlayerMove {
	player: Player;
	move: MoveRequest;
}

export interface AnimationStep {
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
	crystal: Crystal;
}

export interface BoardState {
	board: Crystal[][];
	allowedMoves: MoveRequest[];
	animationSteps: AnimationStep[];
	lastMove?: PlayerMove;
}

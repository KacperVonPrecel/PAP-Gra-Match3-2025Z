import { CrystalType, MoveRequest } from './match3-service';
export interface GameState {
	currentPlayer: Player;
	boardState: BoardState;
}

export interface Crystal {
	crystalType: CrystalType;
	state: CrystalState;
}

export enum CrystalState {
	IDLE = 'idle',
	CREATED = 'created',
	FALLING = 'falling',
	DESTRYED = 'destryed',
	SWAPPED = 'swapped'
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
	board: Crystal[][];
	swapped?: MoveRequest;
	destroyed?: Position[];
	falling?: FallingBlock[];
	newBlocks?: NewBlock[];
}

export interface Position {
	row: number;
	column: number;
}

export interface FallingBlock {
	from: Position;
	to: Position;
}

export interface NewBlock {
	position: Position;
	crystal: Crystal;
}

export interface BoardState {
	board: Crystal[][];
	allowedMoves: MoveRequest[];
	canPlayerMove: boolean;
	animationSteps: AnimationStep[];
	animationIndex?: number;
	lastMove?: PlayerMove;
}

import { Component } from '@angular/core';
import { Board, Match3Service, MoveRequest } from '../match3-service';

@Component({
  selector: 'app-match3',
  imports: [],
  templateUrl: './match3.html',
  styleUrl: './match3.scss',
})
export class Match3 {
  private gameId?: number;

  public board: String = "";
  
  constructor(
    private socket: Match3Service,
  ) { }

  connect(gameId: number): void {
    this.gameId = gameId;
    this.socket.subscribeToGame(this.gameId);
    this.socket.board$.subscribe(board => { this.updateBoard(board) });
    this.fetchBoard();
  }

  disconnect(): void {
    if (this.gameId != undefined) {
      this.socket.unsubscribeFromGame();
      this.gameId = undefined;
    }
  }

  fetchBoard(): void {
    if (this.gameId != undefined)
      this.socket?.updateBoard(this.gameId);
  }

  updateBoard(board: Board): void {
    const boardBlocks = board.board;

    let output = "";
    for (let i = 0; i < boardBlocks.length; i++) {
      for (let j = 0; j < boardBlocks[i].length; j++) {
        output += boardBlocks[i][j].blockType.toString();
      }
      output += "\n";
    }
    this.board = output;
  }

  makeMove(sourceRow: number, sourceColumn: number, targetRow: number, targetColumn: number): void {
    if (this.gameId == undefined)
      return;

    const moveRequest: MoveRequest = {
      sourceRow: sourceRow,
      sourceColumn: sourceColumn,
      targetRow: targetRow,
      targetColumn: targetColumn
    }

    this.socket.swapBlocks(this.gameId, moveRequest);
  }

  destroyMatchedBlocks(): void {
    if (this.gameId != undefined)
      this.socket.destroyMatchedBlocks(this.gameId);
  }

  fillInBlocks(): void {
    if (this.gameId != undefined) {
      this.socket.fillInBlocks(this.gameId);
    }
  }
}

import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root',
})
export class Match3Service {
  private client: Client;
  private subscription?: StompSubscription;

  public board$ = new Subject<Board>();

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
    });

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

    console.log("Unsubscribed from game");
  }

  updateBoard(gameId: number): void {
    this.client.publish({ destination: `/app/board/${gameId}/state`, body: '{}' });
    console.log("board updated");
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

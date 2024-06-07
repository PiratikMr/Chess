package org.example.chess1.Server;

public enum MessageType {
    PLAYER,

    CHECK,
    START,

    BOARD,

    REVENGE_REQUEST,
    REVENGE_ACCEPT,
    REVENGE_CANCEL,

    CLOSE;
}
package com.koenhabets.survur.server;

public class OverwatchPlayerItem {
    private String player;
    private int compRank;
    private int compWinrate;
    private int compGamesPlayed;
    private int quickTimePlayed;
    private int compTimePlayed;
    private int quickGamesWon;
    private int compGamesWon;

    public OverwatchPlayerItem(String player, int compRank, int compWinrate, int compGamesPlayed, int quickTimePlayed, int compTimePlayed, int quickGamesWon, int compGamesWon) {
        this.player = player;
        this.compRank = compRank;
        this.compWinrate = compWinrate;
        this.compGamesPlayed = compGamesPlayed;
        this.quickTimePlayed = quickTimePlayed;
        this.compTimePlayed = compTimePlayed;
        this.quickGamesWon = quickGamesWon;
        this.compGamesWon = compGamesWon;
    }

    public int getCompRank() {
        return compRank;
    }

    public int getCompWinrate() {
        return compWinrate;
    }

    public int getCompGamesPlayed() {
        return compGamesPlayed;
    }

    public int getQuickTimePlayed() {
        return quickTimePlayed;
    }

    public int getCompTimePlayed() {
        return compTimePlayed;
    }

    public int getQuickGamesWon() {
        return quickGamesWon;
    }

    public int getCompGamesWon() {
        return compGamesWon;
    }

    public String getPlayer() {
        return player;
    }
}

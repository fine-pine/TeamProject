import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;

public class Player {
  Piece piece1, piece2, piece3;
  String nickname;
  int win, lose = 0;
  int manaStack = 0;
  ArrayList<Integer> skillList = new ArrayList<Integer>();
  String[] skillIntro = new String[] {"Non-throw (5)", "Move twice (2)", "Together (7)",
      "Back to start (10)", "Only shortcut (3)"};

  public Player() {
    for (int i = 0; i < 5; i++)
      skillList.add(i);
    Collections.shuffle(skillList);
    System.out.println(skillList);

    piece1 = new Piece(skillList.get(0));
    piece2 = new Piece(skillList.get(1));
    piece3 = new Piece(skillList.get(2));
  }

  public Piece getPiece(int n) {
    switch (n) {
      case 1:
        return piece1;
      case 2:
        return piece2;
      case 3:
        return piece3;
      default:
        return piece1;
    }
  }

  // load Player data
  public void loadData(HashMap<String, String> data, String player_ID, String player_PW) {
    this.nickname = player_ID;
    if (data.get(player_ID) == null) {
      data.put(player_ID, player_PW);
    } else {
      if (data.get(String.format("%s_win", player_ID)) != null)
        this.win = Integer.parseInt(data.get(String.format("%s_win", player_ID)));
      if (data.get(String.format("%s_lose", player_ID)) != null)
        this.lose = Integer.parseInt(data.get(String.format("%s_lose", player_ID)));
    }
  }

  // print Player status
  public void printPlayer() {
    System.out.println("nickname: " + nickname);
    System.out.println("Record: " + win + "/" + lose);
    System.out.println("Mana: " + manaStack + "/10");
    System.out.println("Piece 1: " + skillIntro[piece1.skill]);
    System.out.println("Piece 2: " + skillIntro[piece2.skill]);
    System.out.println("Piece 3: " + skillIntro[piece3.skill]);
  }

  void movePiece(int m, int n) {
    int x = getPiece(m).x;
    int y = getPiece(m).y;

    if (x == 6 && y == 6) {
      getPiece(m).move(n);
    } else
      for (int i = 1; i < 4; i++) {
        if (getPiece(i).x == x && getPiece(i).y == y)
          getPiece(i).move(n);
      }
  }
}

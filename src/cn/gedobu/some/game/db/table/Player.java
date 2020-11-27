package cn.gedobu.some.game.db.table;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Player {
	@Id
	private int ID;
	String name;
	private int level;
	Player[] friends;
}

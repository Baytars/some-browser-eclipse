package cn.gedobu.some.game.db.table;

import javax.persistence.Entity;
import javax.persistence.Id;

import cn.gedobu.some.game.db.Scarcity;

@Entity
public class Card {
	@Id
	private int ID;
	Scarcity value;
	String name;
}

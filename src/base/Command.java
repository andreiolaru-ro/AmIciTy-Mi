/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package base;

public class Command {
	public static int	iterator	= 0;

	public static enum Action {
		INJECT, REQUEST, SNAPSHOT, INJECT_ITEM_WANTED, INJECT_ITEM, PAUSE, UNPAUSE, MOVE;
	}

	protected Command.Action	action	= null;
	protected int				time;
	protected int				id;

	public Command(Action action, int time) {
		super();
		this.action = action;
		this.time = time;
		this.id = iterator;
		iterator++;
	}

	@SuppressWarnings("hiding")
	public void setTime(int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	@SuppressWarnings("hiding")
	public void setAction(Command.Action action) {
		this.action = action;
	}

	public Command.Action getAction() {
		return action;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Command [action=" + action + ", time=" + time + "]";
	}

}

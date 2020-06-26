/* CHAT ROOM <MyClass.java>
 * EE422C Project 7 submission by
 * Replace <...> with your actual data.
 * Fawadul Haq
 * fh5277
 * 16225
 * Drew Bernard
 * dhb653
 * 16225
 * Slip days used: 1
 * Spring 2019
 * GitHub: https://github.com/EE422C/project-7-chat-room-pr7-pair-8.git
 */

package assignment7;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends PrintWriter implements Observer {
	public ClientObserver(OutputStream out) {
		super(out);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		this.println(arg); //writer.println(arg);
		this.flush(); //writer.flush();
	}

}

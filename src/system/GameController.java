package system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sysobj.Card;
import sysobj.Player;

public class GameController {
	
	private GameModel model;
	private GameView view;
	
	public GameController(GameModel m, GameView v) {
		this.model = m;
		this.view = v;
		
	    view.setSinglePlayerListener(new SinglePlayerListener());
	    view.setMultiPlayerListener(new MultiPlayerListener());
	    view.setHostGameListener(new HostGameListener());
	    view.setJoinGameListener(new JoinGameListener());
	    view.setDisconnectListener(new DisconnectListener());
	    view.setOptionsListener(new OptionsListener());
	    view.setAboutListener(new AboutListener());
	    view.setLangEnglishListener(new LangEnglishListener());
	    view.setLangFrenchListener(new LangFrenchListener());
	    view.setSoundToggleListener(new SoundToggleListener());
	    view.setMusicToggleListener(new MusicToggleListener());
	    view.setDrawFromLibraryListener(new CardDrawListener());
	}
	
	public void addListenersToPlayerHand(Player player) {
	    for (Card c: player.getHand()) {
	    	for (ActionListener al : c.getActionListeners()) {
	    		c.removeActionListener(al);
	    	}
	    	c.addActionListener(new CardPlayListener());
	    }
	}

	
	private class SinglePlayerListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        model.startGame();
	        int orientation = 0;
	        for (Player p : model.getPlayers()) {
	        	view.updateScoreTable(p);
	        	view.updatePlayerNames(p, orientation);
	        	view.displayCardsInHand(p, orientation++);
	        }
	        addListenersToPlayerHand(model.getActivePlayer());
	        view.displayLastPlayedCard(model.getPlayedCards().get(model.getPlayedCards().size()-1));
	    }
	}
	
    private class CardDrawListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
           System.out.println("Library clicked...");
           model.drawCard();
           view.refreshHand(model.getActivePlayer(), model.getActivePlayer().getOrientation());
           addListenersToPlayerHand(model.getActivePlayer());
        }
    }
    
    private class CardPlayListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Card c = (Card) e.getSource();
			System.out.println(c.toString() + " clicked");
			Player activePlayer = model.getActivePlayer();
			if (model.playCard(c)) {
				for (ActionListener al : c.getActionListeners()) {
					c.removeActionListener(al);
				}
				view.refreshHand(activePlayer, activePlayer.getOrientation());
				view.displayLastPlayedCard(c);
			}
		}
    }

	private class MultiPlayerListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	private class HostGameListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	private class JoinGameListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	private class DisconnectListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	private class OptionsListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	private class AboutListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        view.displayAbout();
	    }
	}

	private class LangEnglishListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	private class LangFrenchListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	private class SoundToggleListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	private class MusicToggleListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	
}

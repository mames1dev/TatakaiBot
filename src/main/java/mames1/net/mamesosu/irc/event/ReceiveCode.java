package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.data.MySQL;
import mames1.net.mamesosu.osu.UserAccount;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReceiveCode extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        // for debug
        System.out.println(e.getMessage());

        MySQL mySQL = new MySQL();
        PreparedStatement ps;
        ResultSet result;
        Connection connection = mySQL.getConnection();
        int userID = UserAccount.getUserID(e.getUser().getNick());

        try {
            ps = connection.prepareStatement("select discord_id, bancho_id from link where code = ?");
            ps.setString(1, e.getMessage());
            result = ps.executeQuery();

            if(result.next()) {
                ps = connection.prepareStatement("update link set bancho_id = ? where code = ?");
                ps.setInt(1, userID);
                ps.setString(2, e.getMessage());
                ps.executeUpdate();
                e.respondWith("アカウントをリンクしました!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

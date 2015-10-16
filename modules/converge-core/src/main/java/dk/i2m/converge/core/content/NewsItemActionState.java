package dk.i2m.converge.core.content;

import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.workflow.Edition;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Tracking data of a {@link NewsItem} in an {@link EditionAction}.
 *
 * @author Raymond Wanyoike
 */
@Entity
@Table(name = "news_item_action_state")
@NamedQueries({
        @NamedQuery(name = NewsItemActionState.FIND_BY_EDITION_NEWSITEM_ACTION, query = "SELECT n FROM NewsItemActionState AS n WHERE n.edition.id = :" + NewsItemActionState.PARAM_EDITION_ID + " AND n.newsItem.id = :" + NewsItemActionState.PARAM_NEWS_ITEM_ID + " AND n.action = :" + NewsItemActionState.PARAM_ACTION),
        @NamedQuery(name = NewsItemActionState.FIND_BY_EDITION_NEWSITEM, query = "SELECT n FROM NewsItemActionState AS n WHERE n.edition.id = :" + NewsItemActionState.PARAM_EDITION_ID + " AND n.newsItem.id = :" + NewsItemActionState.PARAM_NEWS_ITEM_ID),
        @NamedQuery(name = NewsItemActionState.FIND_BY_EDITION, query = "SELECT n FROM NewsItemActionState AS n WHERE n.edition.id = :" + NewsItemActionState.PARAM_EDITION_ID),
        @NamedQuery(name = NewsItemActionState.DELETE_BY_EDITION_NEWSITEM, query = "DELETE FROM NewsItemActionState AS n WHERE n.edition.id = :" + NewsItemActionState.PARAM_EDITION_ID + " AND n.newsItem.id = :" + NewsItemActionState.PARAM_NEWS_ITEM_ID),
        @NamedQuery(name = NewsItemActionState.DELETE_BY_EDITION, query = "DELETE FROM NewsItemActionState AS n WHERE n.edition.id = :" + NewsItemActionState.PARAM_EDITION_ID)
})
public class NewsItemActionState implements Serializable {

    /**
     * Query for locating a {@link NewsItemActionState} based on the Edition, NewsItem and Property name using
     * {@link NewsItemActionState.PARAM_EDITION_ID}, {@link NewsItemActionState.PARAM_NEWS_ITEM_ID} and
     * {@link NewsItemActionState.PARAM_ACTION}.
     */
    public static final String FIND_BY_EDITION_NEWSITEM_ACTION = "NewsItemActionState.findByEditionNewsItemProperty";
    /**
     * Query for locating a {@link NewsItemActionState} based on the Edition, NewsItem using
     * {@link NewsItemActionState.PARAM_EDITION_ID} and {@link NewsItemActionState.PARAM_NEWS_ITEM_ID}.
     */
    public static final String FIND_BY_EDITION_NEWSITEM = "NewsItemActionState.findByEditionNewsItem";
    /**
     * Query for locating a {@link NewsItemActionState} based on the Edition using
     * {@link NewsItemActionState.PARAM_EDITION_ID}.
     */
    public static final String FIND_BY_EDITION = "NewsItemActionState.findByEdition";
    /**
     * Query for deleting {@link NewsItemActionState} based on the Edition, NewsItem using
     * {@link NewsItemActionState.PARAM_EDITION_ID} and {@link NewsItemActionState.PARAM_NEWS_ITEM_ID}.
     */
    public static final String DELETE_BY_EDITION_NEWSITEM = "NewsItemActionState.deleteByEditionNewsItem";
    /**
     * Query for deleting {@link NewsItemActionState} based on the Edition using
     * {@link NewsItemActionState.PARAM_EDITION_ID} and {@link NewsItemActionState.PARAM_NEWS_ITEM_ID}.
     */
    public static final String DELETE_BY_EDITION = "NewsItemActionState.deleteByEdition";
    /**
     * Query parameter for specifying the ID of an Edition.
     */
    public static final String PARAM_EDITION_ID = "editionId";
    /**
     * Query parameter for specifying the ID of a News Item.
     */
    public static final String PARAM_NEWS_ITEM_ID = "newsItemId";
    /**
     * Query parameter for specifying the ID of a Action.
     */
    public static final String PARAM_ACTION = "action";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;

    @ManyToOne
    @JoinColumn(name = "news_item_id")
    private NewsItem newsItem;

    @Column(name = "edition_action")
    private String action;

    @Column(name = "state")
    private String state;

    @Column(name = "data")
    private String data;

    public NewsItemActionState() {
    }

    public NewsItemActionState(Edition edition, NewsItem newsItem, String action, String state, String data) {
        this.edition = edition;
        this.newsItem = newsItem;
        this.action = action;
        this.state = state;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Edition getEdition() {
        return edition;
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
    }

    public NewsItem getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NewsItemActionState)) {
            return false;
        }
        NewsItemActionState other = (NewsItemActionState) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return getClass().getName() + "[ id=" + id + " ]";
    }
}

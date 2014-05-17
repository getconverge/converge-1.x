/*
 * Copyright (C) 2012 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.wiki.Page;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.ejb.services.QueryBuilder;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 * Facade for accessing the wiki features.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class WikiFacadeBean implements WikiFacadeBeanLocal {

    @EJB private DaoServiceLocal daoService;

    @Resource private SessionContext ctx;

    /** {@inheritDoc} */
    @Override
    public List<Page> findSubmenuPages() {
        return daoService.findWithNamedQuery(Page.FIND_SUBMENU);
    }

    /** {@inheritDoc} */
    @Override
    public Page update(Page page) {
        Calendar now = Calendar.getInstance();
        page.setUpdated(now.getTime());
        return daoService.update(page);
    }

    /** {@inheritDoc} */
    @Override
    public Page create(Page page) {
        Calendar now = Calendar.getInstance();
        page.setCreated(now.getTime());
        page.setUpdated(now.getTime());
        return daoService.create(page);
    }

    /** {@inheritDoc} */
    @Override
    public void deletePageById(Long id) {
        daoService.delete(Page.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public Page findPageById(Long id) throws DataNotFoundException {
        return daoService.findById(Page.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public Page findPageByTitle(String title) throws DataNotFoundException {
        return daoService.findObjectWithNamedQuery(Page.class,
                Page.FIND_BY_TITLE, QueryBuilder.with(Page.PARAMETER_TITLE,
                title));
    }
}

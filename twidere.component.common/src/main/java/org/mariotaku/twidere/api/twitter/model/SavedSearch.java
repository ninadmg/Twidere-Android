/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.api.twitter.model;

import java.util.Date;

/**
 * A data interface representing a Saved Search
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.0.8
 */
public interface SavedSearch extends Comparable<SavedSearch>, TwitterResponse {
	Date getCreatedAt();

	int getId();

	String getName();

	int getPosition();

	String getQuery();

}

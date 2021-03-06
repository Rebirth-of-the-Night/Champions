/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.common.affix;

import c4.champions.Champions;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Level;

public class AffixRegistry {

    private static final Map<String, AffixBase> affixMap = Maps.newHashMap();
    private static final Map<AffixCategory, Set<String>> categoryMap = Maps.newEnumMap(AffixCategory.class);

    public static void registerAffix(String identifier, AffixBase affix) {
        affixMap.put(identifier, affix);
        categoryMap.computeIfAbsent(affix.getCategory(), k -> Sets.newHashSet()).add(identifier);
    }

    @Nullable
    public static AffixBase getAffix(String identifier) {
        return affixMap.get(identifier);
    }

    public static ImmutableList<AffixBase> getAllAffixes() {
        return ImmutableList.copyOf(affixMap.values());
    }

    public static ImmutableMap<AffixCategory, Set<String>> getCategoryMap() {
        return ImmutableMap.copyOf(categoryMap);
    }

    public static Set<String> getAffixesForCategory(AffixCategory category) {

        if (categoryMap.containsKey(category)) {
            return Sets.newHashSet(categoryMap.get(category));
        } else {
            Champions.logger.log(Level.ERROR, "No affixes found for category " + category.toString());
            return Sets.newHashSet();
        }
    }
}

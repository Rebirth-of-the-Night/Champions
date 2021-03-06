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

package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AffixDesecrator extends AffixBase {

    public AffixDesecrator() {
        super("desecrator", AffixCategory.OFFENSE);
    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {
        entity.tasks.addTask(0, new AIAttack(entity));
    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt) {

        if (source.getImmediateSource() instanceof EntityAreaEffectCloud && source.getTrueSource() == entity) {
            evt.setCanceled(true);
        }
    }

    class AIAttack extends EntityAIBase {

        private final EntityLiving entity;
        private int attackTime;

        public AIAttack(EntityLiving entityLiving) {
            this.entity = entityLiving;
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = entity.getAttackTarget();
            return isValidAffixTarget(entity, entitylivingbase, true) && entitylivingbase.world.getDifficulty()
                    != EnumDifficulty.PEACEFUL;
        }

        @Override
        public void startExecuting() {
            this.attackTime = ConfigHandler.affix.desecrator.attackInterval;
        }

        @Override
        public void updateTask() {

            if (entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.attackTime;
                EntityLivingBase entitylivingbase = entity.getAttackTarget();

                if (entitylivingbase != null) {
                    entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
                    if (this.attackTime <= 0) {
                        this.attackTime = ConfigHandler.affix.desecrator.attackInterval + entity.getRNG().nextInt(5) * 10;
                        EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(entitylivingbase.world,
                                entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ);
                        cloud.setOwner(this.entity);
                        cloud.setRadius((float)ConfigHandler.affix.desecrator.cloudRadius);
                        cloud.setDuration(ConfigHandler.affix.desecrator.cloudDuration);
                        cloud.setRadiusOnUse(-0.5F);
                        cloud.setWaitTime(ConfigHandler.affix.desecrator.activationTicks);
                        cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());
                        cloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1));
                        entity.world.spawnEntity(cloud);
                    }
                }
                super.updateTask();
            }
        }
    }
}

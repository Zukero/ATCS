package com.gpl.rpg.atcontentstudio.ui.tools;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.gamedata.Item;
import com.gpl.rpg.atcontentstudio.model.gamedata.ItemCategory;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;

public class ItemsTableView extends ElementTableView {

	private static final long serialVersionUID = 1474255176349837609L;

	public ItemsTableView(Project proj) {
		super(new ItemsTableModel(proj), "Compare "+proj.getItemCount()+" items.", new ImageIcon(DefaultIcons.getItemIcon()));
	}
	
	private static class ItemsTableModel implements TableModel {

		Project proj; 
		
		public ItemsTableModel(Project proj) {
			this.proj = proj;
		}
		
		@Override
		public int getRowCount() {
//			return proj.getItemCount() + 1;
			return proj.getItemCount();
		}

		@Override
		public int getColumnCount() {
			return 32;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0: return "Icon"; //Icon
			case 1: return "ID"; //ID
			case 2: return "Name"; //Name
			case 3: return "Folder type"; //Source type (created, altered, source)
			case 4: return "Use type"; //Use type ("none", "use", or equip slot name).
			case 5: return "Category"; //Category id.
			case 6: return "Manually set price ?"; //Has manual price
			case 7: return "Price"; //Price
			case 8: return "On use/hit - HP min";
			case 9: return "On use/hit - HP max";
			case 10: return "On use/hit - AP min";
			case 11: return "On use/hit - AP max";
			case 12: return "On use/hit - # conditions";
			case 13: return "On kill - HP min";
			case 14: return "On kill - HP max";
			case 15: return "On kill - AP min";
			case 16: return "On kill - AP max";
			case 17: return "On kill - # conditions";
			case 18: return "AD min";
			case 19: return "AD max";
			case 20: return "Max HP";
			case 21: return "Max AP";
			case 22: return "Attack cost";
			case 23: return "AC";
			case 24: return "BC";
			case 25: return "DR";
			case 26: return "CS";
			case 27: return "CM";
			case 28: return "Move cost";
			case 29: return "Use cost";
			case 30: return "Reequip cost";
			case 31: return "# conditions";
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
//			return String.class;
			switch (columnIndex) {
			case 0: return Icon.class; // Icon
			case 1: return String.class; //ID
			case 2: return String.class; //Name
			case 3: return String.class; //Source type (created, altered, source)
			case 4: return String.class; //Use type ("none", "use", or equip slot name).
			case 5: return String.class; //Category id.
			case 6: return Boolean.class; //Has manual price
			case 7: return Integer.class; //Price
			case 8: return Integer.class;//"On use/hit - HP min";
			case 9: return Integer.class;//"On use/hit - HP max";
			case 10: return Integer.class;//"On use/hit - AP min";
			case 11: return Integer.class;//"On use/hit - AP max";
			case 12: return Integer.class;//"On use/hit - # conditions";
			case 13: return Integer.class;//"On kill - HP min";
			case 14: return Integer.class;//"On kill - HP max";
			case 15: return Integer.class;//"On kill - AP min";
			case 16: return Integer.class;//"On kill - AP max";
			case 17: return Integer.class;//"On kill - # conditions";
			case 18: return Integer.class;//"AD min";
			case 19: return Integer.class;//"AD max";
			case 20: return Integer.class;//"Max HP";
			case 21: return Integer.class;//"Max AP";
			case 22: return Integer.class;//"Attack cost";
			case 23: return Integer.class;//"AC";
			case 24: return Integer.class;//"BC";
			case 25: return Integer.class;//"DR";
			case 26: return Integer.class;//"CS";
			case 27: return Double.class;//"CM";
			case 28: return Integer.class;//"Move cost";
			case 29: return Integer.class;//"Use cost";
			case 30: return Integer.class;//"Reequip cost";
			case 31: return Integer.class;//"# conditions";
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
//			if (rowIndex == 0) {
//				return getColumnName(columnIndex);
//			}
//			Item item = proj.getItem(rowIndex - 1);
			Item item = proj.getItem(rowIndex);
			boolean canUse = item.category != null && item.category.action_type == ItemCategory.ActionType.use;
			boolean canEquip = item.category != null && item.category.action_type == ItemCategory.ActionType.equip;
			switch (columnIndex) {
			case 0: return new ImageIcon(item.getIcon()); //Icon
			case 1: return item.id; //ID
			case 2: return item.name; //Name
			case 3: return item.getDataType().toString(); //Source type (created, altered, source)
			case 4: //Use type ("none", "use", or equip slot name).
				if (item.category == null) return "none";
				if (item.category.action_type == null) return "none";
				if (item.category.action_type != ItemCategory.ActionType.equip) return item.category.action_type.toString();
				return item.category.slot.toString();
			case 5: return item.category != null ? item.category.id : (item.category_id != null ? item.category_id : null ); //Category id.
			case 6: return item.has_manual_price == null ? false : (item.has_manual_price == 1); //Has manual price
			case 7: //Price
				if (item.has_manual_price == null || item.has_manual_price != 1) return item.computePrice();
				return item.base_market_cost;
			case 8: return canUse ? (item.kill_effect != null ? item.kill_effect.hp_boost_min : null) : (item.hit_effect != null ? item.hit_effect.hp_boost_min : null);//"On use/hit - HP min";
			case 9: return canUse ? (item.kill_effect != null ? item.kill_effect.hp_boost_max : null) : (item.hit_effect != null ? item.hit_effect.hp_boost_max : null);//"On use/hit - HP max";
			case 10: return canUse ? (item.kill_effect != null ? item.kill_effect.ap_boost_min : null) : (item.hit_effect != null ? item.hit_effect.ap_boost_min : null);//"On use/hit - AP min";
			case 11: return canUse ? (item.kill_effect != null ? item.kill_effect.ap_boost_max : null) : (item.hit_effect != null ? item.hit_effect.ap_boost_max : null);//"On use/hit - AP max";
			case 12: //"On use/hit - # conditions";
				if (canUse) {
					if (item.kill_effect != null && item.kill_effect.conditions_source != null) {
						return item.kill_effect.conditions_source.size();
					}
					return 0;
				} else if (item.hit_effect != null) {
					int val = 0;
					if (item.hit_effect.conditions_source != null) {
						val += item.hit_effect.conditions_source.size();
					}
					if (item.hit_effect.conditions_target != null) {
						val += item.hit_effect.conditions_target.size();
					}
					return val;
				}
				return null;
			case 13: return (!canUse && item.kill_effect != null) ? item.kill_effect.hp_boost_min : null;//"On kill - HP min";
			case 14: return (!canUse && item.kill_effect != null) ? item.kill_effect.hp_boost_max : null;//"On kill - HP max";
			case 15: return (!canUse && item.kill_effect != null) ? item.kill_effect.ap_boost_min : null;//"On kill - AP min";
			case 16: return (!canUse && item.kill_effect != null) ? item.kill_effect.ap_boost_max : null;//"On kill - AP max";
			case 17: return (!canUse && item.kill_effect != null && item.kill_effect.conditions_source != null) ? item.kill_effect.conditions_source.size() : null;//"On kill - # conditions";
			case 18: return (canEquip && item.equip_effect != null) ? item.equip_effect.damage_boost_min : null;//"AD min";
			case 19: return (canEquip && item.equip_effect != null) ? item.equip_effect.damage_boost_max : null;//"AD max";
			case 20: return (canEquip && item.equip_effect != null) ? item.equip_effect.max_hp_boost : null;//"Max HP";
			case 21: return (canEquip && item.equip_effect != null) ? item.equip_effect.max_ap_boost : null;//"Max AP";
			case 22: return (canEquip && item.equip_effect != null) ? item.equip_effect.increase_attack_cost : null;//"Attack cost";
			case 23: return (canEquip && item.equip_effect != null) ? item.equip_effect.increase_attack_chance : null;//"AC";
			case 24: return (canEquip && item.equip_effect != null) ? item.equip_effect.increase_block_chance : null;//"BC";
			case 25: return (canEquip && item.equip_effect != null) ? item.equip_effect.increase_damage_resistance : null;//"DR";
			case 26: return (canEquip && item.equip_effect != null) ? item.equip_effect.increase_critical_skill : null;//"CS";
			case 27: return (canEquip && item.equip_effect != null) ? item.equip_effect.critical_multiplier : null;//"CM";
			case 28: return (canEquip && item.equip_effect != null) ? item.equip_effect.increase_move_cost : null;//"Move cost";
			case 29: return (canEquip && item.equip_effect != null) ? item.equip_effect.increase_use_item_cost : null;//"Use cost";
			case 30: return (canEquip && item.equip_effect != null) ? item.equip_effect.increase_reequip_cost : null;//"Reequip cost";
			case 31: return (canEquip && item.equip_effect != null && item.equip_effect.conditions != null) ? item.equip_effect.conditions.size() : null;//"# conditions";
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			//not editable.
		}

		List<TableModelListener> listeners = new ArrayList<TableModelListener>();
		
		@Override
		public void addTableModelListener(TableModelListener l) {
			listeners.add(l);
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			listeners.remove(l);
		}
		
	}

}

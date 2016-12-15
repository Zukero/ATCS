package com.gpl.rpg.atcontentstudio.ui;

import java.awt.Image;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.gpl.rpg.atcontentstudio.Notification;

public class DefaultIcons {

	private static Map<String, Image> imageCache = new LinkedHashMap<String, Image>();
	private static Map<String, Image> iconCache = new LinkedHashMap<String, Image>();

	
	private static String MAIN_ICON_RES = "/com/gpl/rpg/atcontentstudio/img/andorstrainer.png";
	public static Image getMainIconImage() { return getImage(MAIN_ICON_RES); }
	public static Image getMainIconIcon() { return getIcon(MAIN_ICON_RES); }
	
	private static String FOLDER_STD_CLOSED_RES = "/com/gpl/rpg/atcontentstudio/img/folder_std_closed.png";
	public static Image getStdClosedImage() { return getImage(FOLDER_STD_CLOSED_RES); }
	public static Image getStdClosedIcon() { return getIcon(FOLDER_STD_CLOSED_RES); }

	private static String FOLDER_STD_OPEN_RES = "/com/gpl/rpg/atcontentstudio/img/folder_std_open.png";
	public static Image getStdOpenImage() { return getImage(FOLDER_STD_OPEN_RES); }
	public static Image getStdOpenIcon() { return getIcon(FOLDER_STD_OPEN_RES); }

	private static String FOLDER_JSON_CLOSED_RES = "/com/gpl/rpg/atcontentstudio/img/folder_json_closed.png";
	public static Image getJsonClosedImage() { return getImage(FOLDER_JSON_CLOSED_RES); }
	public static Image getJsonClosedIcon() { return getIcon(FOLDER_JSON_CLOSED_RES); }

	private static String FOLDER_JSON_OPEN_RES = "/com/gpl/rpg/atcontentstudio/img/folder_json_open.png";
	public static Image getJsonOpenImage() { return getImage(FOLDER_JSON_OPEN_RES); }
	public static Image getJsonOpenIcon() { return getIcon(FOLDER_JSON_OPEN_RES); }

	private static String FOLDER_SAV_CLOSED_RES = "/com/gpl/rpg/atcontentstudio/img/folder_sav_closed.png";
	public static Image getSavClosedImage() { return getImage(FOLDER_SAV_CLOSED_RES); }
	public static Image getSavClosedIcon() { return getIcon(FOLDER_SAV_CLOSED_RES); }

	private static String FOLDER_SAV_OPEN_RES = "/com/gpl/rpg/atcontentstudio/img/folder_sav_open.png";
	public static Image getSavOpenImage() { return getImage(FOLDER_SAV_OPEN_RES); }
	public static Image getSavOpenIcon() { return getIcon(FOLDER_SAV_OPEN_RES); }

	private static String FOLDER_SPRITE_CLOSED_RES = "/com/gpl/rpg/atcontentstudio/img/folder_sprite_closed.png";
	public static Image getSpriteClosedImage() { return getImage(FOLDER_SPRITE_CLOSED_RES); }
	public static Image getSpriteClosedIcon() { return getIcon(FOLDER_SPRITE_CLOSED_RES); }

	private static String FOLDER_SPRITE_OPEN_RES = "/com/gpl/rpg/atcontentstudio/img/folder_sprite_open.png";
	public static Image getSpriteOpenImage() { return getImage(FOLDER_SPRITE_OPEN_RES); }
	public static Image getSpriteOpenIcon() { return getIcon(FOLDER_SPRITE_OPEN_RES); }

	private static String FOLDER_TMX_CLOSED_RES = "/com/gpl/rpg/atcontentstudio/img/folder_tmx_closed.png";
	public static Image getTmxClosedImage() { return getImage(FOLDER_TMX_CLOSED_RES); }
	public static Image getTmxClosedIcon() { return getIcon(FOLDER_TMX_CLOSED_RES); }

	private static String FOLDER_TMX_OPEN_RES = "/com/gpl/rpg/atcontentstudio/img/folder_tmx_open.png";
	public static Image getTmxOpenImage() { return getImage(FOLDER_TMX_OPEN_RES); }
	public static Image getTmxOpenIcon() { return getIcon(FOLDER_TMX_OPEN_RES); }

	private static String FOLDER_MAP_CLOSED_RES = "/com/gpl/rpg/atcontentstudio/img/folder_map_closed.png";
	public static Image getMapClosedImage() { return getImage(FOLDER_MAP_CLOSED_RES); }
	public static Image getMapClosedIcon() { return getIcon(FOLDER_MAP_CLOSED_RES); }

	private static String FOLDER_MAP_OPEN_RES = "/com/gpl/rpg/atcontentstudio/img/folder_map_open.png";
	public static Image getMapOpenImage() { return getImage(FOLDER_MAP_OPEN_RES); }
	public static Image getMapOpenIcon() { return getIcon(FOLDER_MAP_OPEN_RES); }

	private static String FOLDER_AT_CLOSED_RES = "/com/gpl/rpg/atcontentstudio/img/folder_at_closed.png";
	public static Image getATClosedImage() { return getImage(FOLDER_AT_CLOSED_RES); }
	public static Image getATClosedIcon() { return getIcon(FOLDER_AT_CLOSED_RES); }

	private static String FOLDER_AT_OPEN_RES = "/com/gpl/rpg/atcontentstudio/img/folder_at_open.png";
	public static Image getATOpenImage() { return getImage(FOLDER_AT_OPEN_RES); }
	public static Image getATOpenIcon() { return getIcon(FOLDER_AT_OPEN_RES); }

	private static String TILED_ICON_RES = "/com/gpl/rpg/atcontentstudio/img/tiled-icon.png";
	public static Image getTiledIconImage() { return getImage(TILED_ICON_RES); }
	public static Image getTiledIconIcon() { return getIcon(TILED_ICON_RES); }

	private static String UI_MAP_RES = "/com/gpl/rpg/atcontentstudio/img/ui_icon_map.png";
	public static Image getUIMapImage() { return getImage(UI_MAP_RES); }
	public static Image getUIMapIcon() { return getIcon(UI_MAP_RES); }

	private static String HERO_RES = "/com/gpl/rpg/atcontentstudio/img/char_hero.png";
	public static Image getHeroImage() { return getImage(HERO_RES); }
	public static Image getHeroIcon() { return getIcon(HERO_RES); }
	
	private static String TILE_LAYER_RES = "/com/gpl/rpg/atcontentstudio/img/tile_layer.png";
	public static Image getTileLayerImage() { return getImage(TILE_LAYER_RES); }
	public static Image getTileLayerIcon() { return getIcon(TILE_LAYER_RES); }
	
	private static String OBJECT_LAYER_RES = "/com/gpl/rpg/atcontentstudio/img/object_layer.png";
	public static Image getObjectLayerImage() { return getImage(OBJECT_LAYER_RES); }
	public static Image getObjectLayerIcon() { return getIcon(OBJECT_LAYER_RES); }
	
	private static String ACTOR_CONDITION_RES = "/com/gpl/rpg/atcontentstudio/img/actor_condition.png";
	public static Image getActorConditionImage() { return getImage(ACTOR_CONDITION_RES); }
	public static Image getActorConditionIcon() { return getIcon(ACTOR_CONDITION_RES); }
	
	private static String ITEM_RES = "/com/gpl/rpg/atcontentstudio/img/item.png";
	public static Image getItemImage() { return getImage(ITEM_RES); }
	public static Image getItemIcon() { return getIcon(ITEM_RES); }
	
	private static String NPC_RES = "/com/gpl/rpg/atcontentstudio/img/npc.png";
	public static Image getNPCImage() { return getImage(NPC_RES); }
	public static Image getNPCIcon() { return getIcon(NPC_RES); }
	
	private static String NPC_CLOSE_RES = "/com/gpl/rpg/atcontentstudio/img/npc_close.png";
	public static Image getNPCCloseImage() { return getImage(NPC_CLOSE_RES); }
	public static Image getNPCCloseIcon() { return getIcon(NPC_CLOSE_RES); }
	
	private static String DIALOGUE_RES = "/com/gpl/rpg/atcontentstudio/img/dialogue.png";
	public static Image getDialogueImage() { return getImage(DIALOGUE_RES); }
	public static Image getDialogueIcon() { return getIcon(DIALOGUE_RES); }
	
	private static String QUEST_RES = "/com/gpl/rpg/atcontentstudio/img/ui_icon_quest.png";
	public static Image getQuestImage() { return getImage(QUEST_RES); }
	public static Image getQuestIcon() { return getIcon(QUEST_RES); }
	
	private static String DROPLIST_RES = "/com/gpl/rpg/atcontentstudio/img/ui_icon_equipment.png";
	public static Image getDroplistImage() { return getImage(DROPLIST_RES); }
	public static Image getDroplistIcon() { return getIcon(DROPLIST_RES); }
	
	private static String COMBAT_RES = "/com/gpl/rpg/atcontentstudio/img/ui_icon_combat.png";
	public static Image getCombatImage() { return getImage(COMBAT_RES); }
	public static Image getCombatIcon() { return getIcon(COMBAT_RES); }

	private static String GOLD_RES = "/com/gpl/rpg/atcontentstudio/img/ui_icon_coins.png";
	public static Image getGoldImage() { return getImage(GOLD_RES); }
	public static Image getGoldIcon() { return getIcon(GOLD_RES); }
	
	private static String ITEM_CATEGORY_RES = "/com/gpl/rpg/atcontentstudio/img/equip_weapon.png";
	public static Image getItemCategoryImage() { return getImage(ITEM_CATEGORY_RES); }
	public static Image getItemCategoryIcon() { return getIcon(ITEM_CATEGORY_RES); }
	
	private static String NULLIFY_RES = "/com/gpl/rpg/atcontentstudio/img/nullify.png";
	public static Image getNullifyImage() { return getImage(NULLIFY_RES); }
	public static Image getNullifyIcon() { return getIcon(NULLIFY_RES); }
	
	private static String CREATE_RES = "/com/gpl/rpg/atcontentstudio/img/file_create.png";
	public static Image getCreateImage() { return getImage(CREATE_RES); }
	public static Image getCreateIcon() { return getIcon(CREATE_RES); }
	
	private static String ARROW_UP_RES = "/com/gpl/rpg/atcontentstudio/img/arrow_up.png";
	public static Image getArrowUpImage() { return getImage(ARROW_UP_RES); }
	public static Image getArrowUpIcon() { return getIcon(ARROW_UP_RES); }

	private static String ARROW_DOWN_RES = "/com/gpl/rpg/atcontentstudio/img/arrow_down.png";
	public static Image getArrowDownImage() { return getImage(ARROW_DOWN_RES); }
	public static Image getArrowDownIcon() { return getIcon(ARROW_DOWN_RES); }

	private static String ARROW_LEFT_RES = "/com/gpl/rpg/atcontentstudio/img/arrow_left.png";
	public static Image getArrowLeftImage() { return getImage(ARROW_LEFT_RES); }
	public static Image getArrowLeftIcon() { return getIcon(ARROW_LEFT_RES); }

	private static String ARROW_RIGHT_RES = "/com/gpl/rpg/atcontentstudio/img/arrow_right.png";
	public static Image getArrowRightImage() { return getImage(ARROW_RIGHT_RES); }
	public static Image getArrowRightIcon() { return getIcon(ARROW_RIGHT_RES); }
	
	private static String CONTAINER_RES = "/com/gpl/rpg/atcontentstudio/img/container.png";
	public static Image getContainerImage() { return getImage(CONTAINER_RES); }
	public static Image getContainerIcon() { return getIcon(CONTAINER_RES); }
	
	private static String KEY_RES = "/com/gpl/rpg/atcontentstudio/img/key.png";
	public static Image getKeyImage() { return getImage(KEY_RES); }
	public static Image getKeyIcon() { return getIcon(KEY_RES); }
	
	private static String MAPCHANGE_RES = "/com/gpl/rpg/atcontentstudio/img/mapchange.png";
	public static Image getMapchangeImage() { return getImage(MAPCHANGE_RES); }
	public static Image getMapchangeIcon() { return getIcon(MAPCHANGE_RES); }
	
	private static String REPLACE_RES = "/com/gpl/rpg/atcontentstudio/img/replace.png";
	public static Image getReplaceImage() { return getImage(REPLACE_RES); }
	public static Image getReplaceIcon() { return getIcon(REPLACE_RES); }
	
	private static String REST_RES = "/com/gpl/rpg/atcontentstudio/img/rest.png";
	public static Image getRestImage() { return getImage(REST_RES); }
	public static Image getRestIcon() { return getIcon(REST_RES); }
	
	private static String SCRIPT_RES = "/com/gpl/rpg/atcontentstudio/img/script.png";
	public static Image getScriptImage() { return getImage(SCRIPT_RES); }
	public static Image getScriptIcon() { return getIcon(SCRIPT_RES); }
	
	private static String SIGN_RES = "/com/gpl/rpg/atcontentstudio/img/sign.png";
	public static Image getSignImage() { return getImage(SIGN_RES); }
	public static Image getSignIcon() { return getIcon(SIGN_RES); }
	
	private static String CREATE_CONTAINER_RES = "/com/gpl/rpg/atcontentstudio/img/create_container.png";
	public static Image getCreateContainerImage() { return getImage(CREATE_CONTAINER_RES); }
	public static Image getCreateContainerIcon() { return getIcon(CREATE_CONTAINER_RES); }
	
	private static String CREATE_KEY_RES = "/com/gpl/rpg/atcontentstudio/img/create_key.png";
	public static Image getCreateKeyImage() { return getImage(CREATE_KEY_RES); }
	public static Image getCreateKeyIcon() { return getIcon(CREATE_KEY_RES); }
	
	private static String CREATE_REPLACE_RES = "/com/gpl/rpg/atcontentstudio/img/create_replace.png";
	public static Image getCreateReplaceImage() { return getImage(CREATE_REPLACE_RES); }
	public static Image getCreateReplaceIcon() { return getIcon(CREATE_REPLACE_RES); }

	private static String CREATE_REST_RES = "/com/gpl/rpg/atcontentstudio/img/create_rest.png";
	public static Image getCreateRestImage() { return getImage(CREATE_REST_RES); }
	public static Image getCreateRestIcon() { return getIcon(CREATE_REST_RES); }

	private static String CREATE_SCRIPT_RES = "/com/gpl/rpg/atcontentstudio/img/create_script.png";
	public static Image getCreateScriptImage() { return getImage(CREATE_SCRIPT_RES); }
	public static Image getCreateScriptIcon() { return getIcon(CREATE_SCRIPT_RES); }

	private static String CREATE_SIGN_RES = "/com/gpl/rpg/atcontentstudio/img/create_sign.png";
	public static Image getCreateSignImage() { return getImage(CREATE_SIGN_RES); }
	public static Image getCreateSignIcon() { return getIcon(CREATE_SIGN_RES); }
	
	private static String CREATE_SPAWNAREA_RES = "/com/gpl/rpg/atcontentstudio/img/create_spawnarea.png";
	public static Image getCreateSpawnareaImage() { return getImage(CREATE_SPAWNAREA_RES); }
	public static Image getCreateSpawnareaIcon() { return getIcon(CREATE_SPAWNAREA_RES); }

	private static String CREATE_MAPCHANGE_RES = "/com/gpl/rpg/atcontentstudio/img/create_tiled.png";
	public static Image getCreateMapchangeImage() { return getImage(CREATE_MAPCHANGE_RES); }
	public static Image getCreateMapchangeIcon() { return getIcon(CREATE_MAPCHANGE_RES); }
	
	private static String CREATE_OBJECT_GROUP_RES = "/com/gpl/rpg/atcontentstudio/img/create_object_group.png";
	public static Image getCreateObjectGroupImage() { return getImage(CREATE_OBJECT_GROUP_RES); }
	public static Image getCreateObjectGroupIcon() { return getIcon(CREATE_OBJECT_GROUP_RES); }

	private static String CREATE_TILE_LAYER_RES = "/com/gpl/rpg/atcontentstudio/img/create_tile_layer.png";
	public static Image getCreateTileLayerImage() { return getImage(CREATE_TILE_LAYER_RES); }
	public static Image getCreateTileLayerIcon() { return getIcon(CREATE_TILE_LAYER_RES); }

	private static String ZOOM_RES = "/com/gpl/rpg/atcontentstudio/img/zoom.png";
	public static Image getZoomImage() { return getImage(ZOOM_RES); }
	public static Image getZoomIcon() { return getIcon(ZOOM_RES); }
	
	private static Image getImage(String res) {
		if (imageCache.get(res) == null) {
			try {
				Image img = ImageIO.read(DefaultIcons.class.getResourceAsStream(res));
				imageCache.put(res, img);
			} catch (IOException e) {
				Notification.addError("Failed to load image "+res);
				e.printStackTrace();
			}
		}
		return imageCache.get(res);
	}
	
	private static Image getIcon(String res) {
		if (iconCache.get(res) == null) {
			Image icon = getImage(res).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
			iconCache.put(res, icon);
		}
		return iconCache.get(res);
	}
}


package org.godotengine.godot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.view.View;
import android.os.Bundle;

import com.google.android.gms.games.SnapshotsClient;

import org.godotengine.godot.GodotAndroidRequest;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONException;

// Import google play service
import org.godotengine.godot.google.GoogleAchievements;
import org.godotengine.godot.google.GoogleAuthentication;
import org.godotengine.godot.google.GoogleLeaderboard;
import org.godotengine.godot.google.GoogleSnapshot;

// Import facebook
import org.godotengine.godot.facebook.FacebookAuthentication;
import org.godotengine.godot.facebook.FacebookShare;

// Import firebase
import org.godotengine.godot.firebase.FirebaseCurrentUser;
import org.godotengine.godot.firebase.FirebaseCurrentAnalytics;

public class GodotAndroid extends Godot.SingletonBase {

	private static Context context;
	private static Activity activity;

	private GoogleAchievements googleAchievements;
	private GoogleAuthentication googleAuthentication;
	private GoogleLeaderboard googleLeaderboard;
	private GoogleSnapshot googleSnapshot;

	private FacebookAuthentication facebookAuthentication;
	private FacebookShare facebookShare;

	private FirebaseCurrentUser firebaseCurrentUser;
	private FirebaseCurrentAnalytics firebaseCurrentAnalytics;

	private static final HashMap<String, Integer> GOOGLE_SNAPSHOT_RESOLUTION_POLICIES;

	static {
		GOOGLE_SNAPSHOT_RESOLUTION_POLICIES = new HashMap<String, Integer>();

		GOOGLE_SNAPSHOT_RESOLUTION_POLICIES.put("RESOLUTION_POLICY_HIGHEST_PROGRESS", new Integer(SnapshotsClient.RESOLUTION_POLICY_HIGHEST_PROGRESS));
		GOOGLE_SNAPSHOT_RESOLUTION_POLICIES.put("RESOLUTION_POLICY_LAST_KNOWN_GOOD", new Integer(SnapshotsClient.RESOLUTION_POLICY_LAST_KNOWN_GOOD));
		GOOGLE_SNAPSHOT_RESOLUTION_POLICIES.put("RESOLUTION_POLICY_LONGEST_PLAYTIME", new Integer(SnapshotsClient.RESOLUTION_POLICY_LONGEST_PLAYTIME));
		GOOGLE_SNAPSHOT_RESOLUTION_POLICIES.put("RESOLUTION_POLICY_MANUAL", new Integer(SnapshotsClient.RESOLUTION_POLICY_MANUAL));
		GOOGLE_SNAPSHOT_RESOLUTION_POLICIES.put("RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED", new Integer(SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED));
	};

	static public Godot.SingletonBase initialize (Activity p_activity) {
		return new GodotAndroid(p_activity);
	}

	public GodotAndroid(Activity p_activity) {
		registerClass ("GodotAndroid", new String[] {
			// Google's services
			"google_initialize",

			// GoogleAuthentication
			"google_connect", "google_disconnect", "google_is_connected",

			// GoogleLeaderboard
			"google_leaderboard_submit", "google_leaderboard_show", "google_leaderboard_showlist",

			// GoogleSnapshot
			"google_snapshot_load", "google_snapshot_save",

			// GoogleAchievements
			"google_achievement_unlock", "google_achievement_increment", "google_achievement_show_list",

			// Facebook
			"facebook_initialize",

			// FacebookAuthentication
			"facebook_connect", "facebook_disconnect", "facebook_is_connected",

			// FacebookShare
			"facebook_share_link", "facebook_share_link_with_quote", "facebook_share_link_with_quote_and_hashtag",

			// Firebase
			"firebase_initialize",

			// FirebaseCurrentUser
			"firebase_get_user_details"

			// FirebaseCurrentAnalytics
			"firebase_log_event", "firebase_tutorial_begin", "firebase_tutorial_complete", "firebase_purchase",
			"firebase_unlock_achievement", "firebase_join_group", "firebase_login", "firebase_level_up", 
			"firebase_post_score", "firebase_select_content", "firebase_share"
		});

		activity = p_activity;
	}

	public HashMap<String, Integer> get_google_resolution_policies() {
		return GOOGLE_SNAPSHOT_RESOLUTION_POLICIES;
	}

	public void firebase_initialize(final int instance_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
					firebaseCurrentUser = FirebaseCurrentUser.getInstance(activity);
					firebaseCurrentUser.init(instance_id);

					firebaseCurrentAnalytics = FirebaseCurrentAnalytics.getInstance(activity);
					firebaseCurrentAnalytics.init(instance_id);
			}
		});
	}

	public String firebase_get_user_details() {
		return firebaseCurrentUser.get_user_details();
	}

	public void firebase_log_event(final String event_name, final HashMap<String, Object> params) {
		firebaseCurrentAnalytics.firebase_log_event(event_name, params);
	}

	public void firebase_tutorial_begin(final String name) {
		firebaseCurrentAnalytics.firebase_tutorial_begin(name);
	}

	public void firebase_tutorial_complete(final String name) {
		firebaseCurrentAnalytics.firebase_tutorial_complete(name);
	}

	public void firebase_purchase(final String item) {
		firebaseCurrentAnalytics.firebase_purchase(item);
	}

	public void firebase_unlock_achievement(final String achievement) {
		firebaseCurrentAnalytics.firebase_unlock_achievement(achievement);
	}

	public void firebase_join_group(final String group) {
		firebaseCurrentAnalytics.firebase_join_group(group);
	}

	public void firebase_login() {
		firebaseCurrentAnalytics.firebase_login();
	}

	public void firebase_level_up(final String name) {
		firebaseCurrentAnalytics.firebase_level_up(name);
	}

	public void firebase_post_score(final int score) {
		firebaseCurrentAnalytics.firebase_post_score(score);
	}

	public void firebase_select_content(final String name) {
		firebaseCurrentAnalytics.firebase_select_content(name);
	}

	public void firebase_share() {
		firebaseCurrentAnalytics.firebase_share();
	}

	public void google_initialize(final int instance_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleAchievements = GoogleAchievements.getInstance(activity);
				googleAchievements.init(instance_id);

				googleAuthentication = GoogleAuthentication.getInstance(activity);
				googleAuthentication.init(instance_id);

				googleLeaderboard = GoogleLeaderboard.getInstance(activity);
				googleLeaderboard.init(instance_id);

				googleSnapshot = GoogleSnapshot.getInstance(activity);
				googleSnapshot.init(instance_id);
			}
		});
	}

	public void facebook_initialize(final int instance_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
					facebookAuthentication = FacebookAuthentication.getInstance(activity);
					facebookAuthentication.init(instance_id);

					facebookShare = FacebookShare.getInstance(activity);
					facebookShare.init(instance_id);
			}
		});
	}

	public void facebook_connect() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				facebookAuthentication.connect();
			}
		});
	}

	public void facebook_disconnect() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				facebookAuthentication.disconnect();
			}
		});
	}

	public boolean facebook_is_connected() {
		return facebookAuthentication.isConnected();
	}

	public void facebook_share_link(final String link) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				facebookShare.share_link(link);
			}
		});
	}

	public void facebook_share_link_with_quote(final String link, final String quote) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				facebookShare.share_link(link, quote);
			}
		});
	}

	public void facebook_share_link_with_quote_and_hashtag(final String link, final String quote, final String hashtag) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				facebookShare.share_link(link, quote, hashtag);
			}
		});
	}

	public void google_connect() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleAuthentication.connect();
			}
		});
	}

	public void google_disconnect() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleAuthentication.disconnect();
			}
		});
	}

	public boolean google_is_connected() {
		return googleAuthentication.isConnected();
	}

	// Google Leaderboards
	public void google_leaderboard_submit(final String id, final int score) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleLeaderboard.leaderboard_submit(id, score);
			}
		});
	}

	public void google_leaderboard_show(final String id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleLeaderboard.leaderboard_show(id);
			}
		});
	}

	public void google_leaderboard_showlist() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleLeaderboard.leaderboard_showlist();
			}
		});
	}

	// Google snapshots
	public void google_snapshot_load(final String snapshotName, final int conflictResolutionPolicy) {
		googleSnapshot.snapshot_load(snapshotName, conflictResolutionPolicy);
	}

	public void google_snapshot_save(final String snapshotName, final String data, final String description, final boolean flag_force) {
		googleSnapshot.snapshot_save(snapshotName, data, description, flag_force);
	}

	// Google achievements
	public void google_achievement_unlock(final String id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleAchievements.achievement_unlock(id);
			}
		});
	}

	public void google_achievement_increment(final String id, final int amount) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleAchievements.achievement_increment(id, amount);
			}
		});
	}

	public void google_achievement_show_list() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				googleAchievements.achievement_show_list();
			}
		});
	}

	protected void onMainActivityResult (int requestCode, int resultCode, Intent data) {
		// Trigger google's services
		googleAchievements.onActivityResult(requestCode, resultCode, data);
		googleAuthentication.onActivityResult(requestCode, resultCode, data);
		googleLeaderboard.onActivityResult(requestCode, resultCode, data);
		googleSnapshot.onActivityResult(requestCode, resultCode, data);

		// Trigger Facebook
		facebookAuthentication.onActivityResult(requestCode, resultCode, data);
		facebookShare.onActivityResult(requestCode, resultCode, data);
	}

	protected void onMainPause () {
		// Trigger google's services
		googleAchievements.onPause();
		googleAuthentication.onPause();
		googleLeaderboard.onPause();
		googleSnapshot.onPause();

		// Trigger Facebook
		facebookAuthentication.onPause();
		facebookShare.onPause();
	}

	protected void onMainResume () {
		// Trigger google's services
		googleAchievements.onResume();
		googleAuthentication.onResume();
		googleLeaderboard.onResume();
		googleSnapshot.onResume();

		// Trigger Facebook
		facebookAuthentication.onResume();
		facebookShare.onResume();
	}

	protected void onMainDestroy () {
		// Trigger google's services
		googleAchievements.onStop();
		googleAuthentication.onStop();
		googleLeaderboard.onStop();
		googleSnapshot.onStop();

		// Trigger Facebook
		facebookAuthentication.onStop();
		facebookShare.onStop();
	}
}
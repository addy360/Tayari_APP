package com.lockminds.tayari.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant")
data class Restaurant(
        @PrimaryKey(autoGenerate = false) var business_id: Long,
        var business_key: String? = "",
        var business_name: String? = "",
        var business_address: String? = "",
        var business_telephone: String? = "",
        var business_fax: String? = "",
        var business_tagline: String? = "",
        var business_email: String? = "",
        var business_location: String? = "",
        var business_tin: String? = "",
        var business_box: String? = "",
        var business_backup_email: String? = "",
        var business_website: String? = "",
        var business_logo: String? = "",
        var business_banner: String? = "",
        var business_created_at: String? = "",
        var business_created_by: String? = "",
        var business_modified_on: String? = "",
        var business_modified_by: String? = "",
        var business_deleted: String? = "",
        var business_deleted_by: String? = "",
        var business_work_status: String? = "",
        var business_welcome_note: String? = "",
        var business_longitude: String? = "",
        var business_latitude: String? = "",
 ){
    constructor(): this(
        1,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
            "",
            ""
    )
}

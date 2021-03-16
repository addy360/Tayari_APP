package com.lockminds.tayari.datasource.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurants(
    @PrimaryKey(autoGenerate = false) var business_id: String,
    var business_key: String? = null,
    var business_name: String? = null,
    var business_address: String? = null,
    var business_telephone: String? = null,
    var business_fax: String? = null,
    var business_tagline: String? = null,
    var business_email: String? =null,
    var business_location: String? =null ,
    var business_tin: String? = null ,
    var business_box: String? = null,
    var business_backup_email: String? = null,
    var business_website: String? = null,
    var business_logo: String? = null,
    var business_banner: String? = null,
    var business_created_at: String? = null,
    var business_created_by: String? =  null,
    var business_modified_on: String? =  null,
    var business_modified_by: String? = null,
    var business_deleted: String? =  null,
    var business_deleted_by: String? =  null,
    var business_work_status: String? = null,
    var business_welcome_note: String?=null,
    var business_longitude: String? = null,
    var business_latitude: String? = null
){
    constructor() : this(
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
        "",
        "",

    )

}
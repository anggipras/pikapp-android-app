package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualAddAdvMenuFragment

enum class AdvanceMenuTemplateType {
    CHECKBOX, RADIO;

    override fun toString(): String = if (this == CHECKBOX) "CHECKBOX" else "RADIO"
    fun isMandatory(): Boolean = this != CHECKBOX
}

/*ADD NEW ADVANCE MENU AND EXTRA MENU ON EDIT START*/
data class AddNewAdvanceMenu(
        @SerializedName("product_id")
        var product_id: String,
        @SerializedName("advance_menu")
        var advance_menu: AdvanceMenu
)

data class ListNewAdvanceMenuResponse(
        @SerializedName("err_code")
        var errorCode: String,
        @SerializedName("err_message")
        var errorMessage: String,
        @SerializedName("results")
        var results: AdvanceMenu
)

data class AddNewExtraMenu(
        @SerializedName("advance_menu_id")
        var advance_menu_id: Long,
        @SerializedName("ext_menu_name")
        var ext_menu_name: String,
        @SerializedName("ext_menu_price")
        var ext_menu_price: String,
        @SerializedName("active")
        var active: Boolean,
        @SerializedName("product_id")
        var product_id: String
)

data class NewExtraMenuResponse(
        @SerializedName("err_code")
        var errorCode: String,
        @SerializedName("err_message")
        var errorMessage: String,
        @SerializedName("results")
        var results: AddNewExtraMenu
)
/*ADD NEW ADVANCE MENU ON EDIT END*/

/*ADD ADVANCE MENU START*/
data class AddAdvanceMenuRequest(
    @SerializedName("advance_menus")
    var advance_menus: List<AdvanceMenu>
)

data class AddAdvanceMenuResponse(
    @SerializedName("err_code")
    var errorCode: String,
    @SerializedName("err_message")
    var errorMessage: String,

    @SerializedName("results")
    var result: List<AdvanceMenu>
)

data class ListAdvanceMenuResponse(
    @SerializedName("err_code")
    var errorCode: String,
    @SerializedName("err_message")
    var errorMessage: String,

    @SerializedName("results")
    var results: List<AdvanceMenu>
)

data class AdvanceMenu(
        @SerializedName("template_name")
        var template_name: String,
        @SerializedName("template_type")
        var template_type: String,

        @SerializedName("active")
        var active: Boolean,
        @SerializedName("mandatory")
        var mandatory: Boolean,

        @SerializedName("max_choose")
        var max_choose: Int,
        @SerializedName("ext_menus")
        var ext_menus: List<AdvanceAdditionalMenu>
)

data class AdvanceAdditionalMenu(
        @SerializedName("ext_menu_name")
        var ext_menu_name: String,
        @SerializedName("ext_menu_price")
        var ext_menu_price: String,
        @SerializedName("active")
        var active: Boolean
)
/*ADD ADVANCE MENU END*/

/*EDIT ADVANCE MENU START*/
data class EditAdvanceMenuRequest(
        @SerializedName("advance_menus")
        var advance_menus: List<AdvanceMenuEdit>
)

data class ListAdvanceMenuEditResponse(
        @SerializedName("err_code")
        var errorCode: String,
        @SerializedName("err_message")
        var errorMessage: String,

        @SerializedName("results")
        var results: List<AdvanceMenuEdit>
)

data class ListAdvanceMenuEditResp(
        @SerializedName("err_code")
        var errorCode: String,
        @SerializedName("err_message")
        var errorMessage: String,

        @SerializedName("results")
        var results: AdvanceMenuEdit
)

data class AdvanceMenuEdit(
        @SerializedName("product_id")
        var product_id: String? = null,
        @SerializedName("template_name")
        var template_name: String,
        @SerializedName("template_type")
        var template_type: String,

        @SerializedName("active")
        var active: Boolean,
        @SerializedName("mandatory")
        var mandatory: Boolean,

        @SerializedName("max_choose")
        var max_choose: Int,
        @SerializedName("id")
        var id: Long,
        @SerializedName("ext_menus")
        var ext_menus: List<AdvanceAdditionalMenuEdit>
)

data class AdvanceAdditionalMenuEdit(
        @SerializedName("ext_menu_name")
        var ext_menu_name: String,
        @SerializedName("ext_menu_price")
        var ext_menu_price: String,
        @SerializedName("active")
        var active: Boolean,
        @SerializedName("ext_id")
        var ext_id: Long
)
/*EDIT ADVANCE MENU END*/

/*ADD MANUAL TXN ADVANCE MENU START*/
data class AddManualAdvMenu(
        @SerializedName("product_Id")
        var product_id: String,
        @SerializedName("food_name")
        var foodName: String,
        @SerializedName("food_category")
        var foodCategory: String,
        @SerializedName("food_amount")
        var foodAmount: Int,
        @SerializedName("food_price")
        var foodPrice: Int,
        @SerializedName("foodList_Checkbox")
        var foodListCheckbox: List<FoodListCheck?>,
        @SerializedName("foodList_Radio")
        var foodListRadio: List<FoodListRadio?>,
        @SerializedName("food_note")
        var foodNote: String?,
        @SerializedName("foodTotalPrice")
        var foodTotalPrice: String
)

data class FoodListCheck(
        @SerializedName("name")
        var name: String,
        @SerializedName("price")
        var price: Int
)

data class FoodListRadio(
        @SerializedName("name")
        var name: String,
        @SerializedName("price")
        var price: Int
)
/*ADD MANUAL TXN ADVANCE MENU END*/
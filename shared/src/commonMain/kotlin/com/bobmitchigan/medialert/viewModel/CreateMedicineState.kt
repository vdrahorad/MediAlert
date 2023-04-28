package com.bobmitchigan.medialert.viewModel

import com.bobmitchigan.medialert.MR
import com.bobmitchigan.medialert.domain.BlisterPack
import com.bobmitchigan.medialert.domain.Medicine
import com.bobmitchigan.medialert.domain.createNewBlisterPack

data class CreateMedicineState(
    val name: InputState<String> = InputState(),
    val blisterPackCount: InputState<Int> = InputState(),
    val areAllPacksIdentical: Boolean = true,
    val dimensions: List<BlisterPackDimension> = listOf(),
) {
    fun toMedicine() = runCatching {
        Medicine(
            name = name.value!!,
            blisterPacks = if (areAllPacksIdentical) mapFirstDimension() else mapDimensionsToFilledPacks(),
            schedule = listOf()
        )
    }.getOrNull()

    private fun mapFirstDimension(): List<BlisterPack> {
        return (0 until blisterPackCount.value!!).map {
            createNewBlisterPack(
                rows = dimensions.first().rowCount.value!!,
                dimensions.first().columnCount.value!!
            )
        }.apply { require(isNotEmpty()) }
    }

    private fun mapDimensionsToFilledPacks() = dimensions.map {
        createNewBlisterPack(rows = it.rowCount.value!!, columns = it.columnCount.value!!)
    }.apply { require(isNotEmpty() && isValid()) }

    fun validate() = CreateMedicineState(
        name.copy(error = MR.strings.create_medicine_mandatory_field.takeIf { name.value.isNullOrEmpty() }),
        blisterPackCount.copy(
            error = MR.strings.create_medicine_mandatory_field.takeIf {
                blisterPackCount.value.isNullOrZero()
            }),
        areAllPacksIdentical,
        dimensions.map { dimension ->
            BlisterPackDimension(
                dimension.rowCount.copy(
                    error = MR.strings.create_medicine_mandatory_field.takeIf {
                        dimension.rowCount.value.isNullOrZero()
                    }),
                dimension.columnCount.copy(error = MR.strings.create_medicine_mandatory_field.takeIf {
                    dimension.columnCount.value.isNullOrZero()
                })
            )
        }
    )
}

private fun List<BlisterPack>.isValid(): Boolean {
    return all { pack -> pack.rows.isNotEmpty() && pack.rows.all { it.value.isNotEmpty() } }
}

private fun Int?.isNullOrZero() = this == null || this == 0

data class BlisterPackDimension(
    val rowCount: InputState<Int> = InputState(),
    val columnCount: InputState<Int> = InputState(),
)

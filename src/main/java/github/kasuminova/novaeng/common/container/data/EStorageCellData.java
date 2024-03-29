package github.kasuminova.novaeng.common.container.data;

import com.github.bsideup.jabel.Desugar;
import github.kasuminova.novaeng.common.block.estorage.prop.DriveStorageLevel;
import github.kasuminova.novaeng.common.block.estorage.prop.DriveStorageType;

@Desugar
public record EStorageCellData(DriveStorageType type, DriveStorageLevel level, int usedTypes, long usedBytes) {
}

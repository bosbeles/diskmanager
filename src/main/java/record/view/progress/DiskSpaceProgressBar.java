package record.view.progress;

import record.util.FileUnit;
import record.repo.DiskStats;

public class DiskSpaceProgressBar extends CustomProgressBar {


    public void updateDiskStats(DiskStats stats) {

        DiskStats.DiskParameters diskParameters = stats.getDiskParameters();
        long usedSize = stats.getUsedSize();
        long totalSize = diskParameters.getTotalSize();

        String usedText = FileUnit.humanReadable(usedSize);
        String totalText = FileUnit.humanReadable(totalSize);

        this.setProgressText(usedText + " / " + totalText);

        if (usedSize >= diskParameters.getErrorSize()) {
            this.setProgressColor(CustomProgressBar.ERROR);
        } else if (usedSize >= diskParameters.getWarnSize()) {
            this.setProgressColor(CustomProgressBar.WARN);
        } else {
            this.setProgressColor(CustomProgressBar.NORMAL);
        }

        this.setProgressValue((int) (usedSize * 100 / totalSize));

    }

}

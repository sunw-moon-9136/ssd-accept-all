import org.junit.jupiter.api.Test;

class SsdTest {


    //Write Test
    @Test
    void LBA_영역_값_쓰기_ssd_nand_txt_미존재() {
    }


    @Test
    void LBA_영역_값_쓰기_ssd_nand_txt_존재() {
    }


    //Read TEst
    @Test
    void 기록한적_있는_LBA영역_읽기() {
        //• Read 명령어는 ssd_nand.txt에서 데이터를 읽고,
        //읽은 데이터를 ssd_output.txt 파일에 기록한다.
    }

    @Test
    void 기록한적_없는_LBA영역_읽기() {
        //• 기록이 한적이 없는 LBA를 읽으면 0x00000000 으로 읽힌다.
    }

    @Test
    void 같은_LBA영역_다른_값_쓰고_읽기_두번() {
        //    • ssd_output.txt에는 항상 마지막 Read 명령어에 대한 수행 결과가 저장되어있다.
        //    즉, 덮어쓰기 방식으로 파일에 읽은 값을 기록한다.
    }

}

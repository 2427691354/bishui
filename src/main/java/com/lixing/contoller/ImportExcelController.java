package com.lixing.contoller;

import com.lixing.entity.*;
import com.lixing.service.EqService;
import com.lixing.service.UserService;
import com.lixing.service.WxUserService;
import com.mongodb.client.result.UpdateResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author cc
 * @date 2020/07/13
 **/
@RestController
public class ImportExcelController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;

    @Autowired
    private EqService eqService;

    @Autowired
    private WxUserService wxUserService;


    //    人员导入
    @PostMapping("/import")
    public String importXXXXXXWords(@RequestParam("file") MultipartFile file, @RequestParam(value = "type",required = false) Integer type, HttpServletResponse response) throws Exception {

        System.err.println("******************");
        if (file == null) {
            return "文件不存在";
        }

        String fileName = file.getOriginalFilename();
        //获取文件名后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
            InputStream is = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            //有多少个sheet
            int sheets = workbook.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                //获取多少行
                int rows = sheet.getPhysicalNumberOfRows();
                User h = null;



                //遍历每一行，注意：第 0 行为标题
                for (int j = 1; j < rows; j++) {
                    //                for (int j = 1; j < 2; j++) {
                    h = new User();
                    XSSFRow row = sheet.getRow(j);

                    h.setType(type);



                    Map<String,Object> map = new HashMap<>();
                    if(type == 0){  // 总平台
                        map.put("user_name","ceshigl");
                        map.put("pwd","2219992156");
                        map.put("nick_name",row.getCell(4).getStringCellValue());
                        h.setIsAdmin(true);
                        row.getCell(8).setCellType(Cell.CELL_TYPE_NUMERIC);
                        h.setTotalRevenue(row.getCell(8).getNumericCellValue());
                        h.setWithdrawal(0.0);
                        h.setWxId("");
                    }
                    if(type == 1){  // 县级代理
                        row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                        map.put("user_name",row.getCell(2).getStringCellValue());
                        map.put("pwd","123456");
                        row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                        map.put("agent_nick",row.getCell(4).getStringCellValue());

                        row.getCell(9).setCellType(Cell.CELL_TYPE_NUMERIC);
                        map.put("secret_key",row.getCell(9).getNumericCellValue());
                        row.getCell(8).setCellType(Cell.CELL_TYPE_NUMERIC);
                        h.setTotalRevenue(row.getCell(8).getNumericCellValue());
                        h.setWithdrawal(0.0);
                        map.put("is_active",false);
                        h.setIsAdmin(true);

                        WxUser byName = wxUserService.findByName(row.getCell(4).getStringCellValue());
                        if(byName!=null){
                            h.setWxId(byName.getId());
                        }
                        else{
                            h.setWxId("");
                        }
                    }
                    if(type == 2){  // 管理员

                        row.getCell(3).setCellType(Cell.CELL_TYPE_NUMERIC);
                        h.setTotalRevenue(row.getCell(3).getNumericCellValue());
                        h.setWithdrawal(0.0);

                        Eq byEqName = eqService.findByEqName(row.getCell(5).getStringCellValue());
                        if(byEqName!=null){
                            map.put("eq_id",byEqName.getEq_id());
                        }

                        h.setIsAdmin(false);
                        h.setWxId("");
                    }

                    h.setInfo(map);

                    User insert = mongoTemplate.insert(h);
                    if(insert!=null){
                        sheet.removeRow(row);
                    }
                }

                // 删除空白行
                int lastRowNum = sheet.getLastRowNum();
                for(int k =1;k<lastRowNum;k++){
                    Row r = sheet.getRow(k);
                    Cell cell = null;
                    String cellValue  = "";
                    if (r!=null){
                        cell = r.getCell(0);
                    }
                    if (r==null){
                        sheet.shiftRows(k+1,lastRowNum,-1);
                        k--;
                        //减去一条空行，总行数减一。
                        lastRowNum--;
                    }
                }

//                    FileOutputStream os = new FileOutputStream("C:\\Users\\陈晨\\Desktop\\"+fileName);
                OutputStream os = response.getOutputStream();
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
//                    FileOutputStream os = new FileOutputStream(response.getOutputStream());
//
                workbook.write(os);
                os.close();
            }
            workbook.close();
            is.close();

            return "导入数据成功";

        } else {
            return "文件格式不正确";
        }
    }

    // 服务站导入
    @PostMapping("/import2")
    public String importXXXXXXWords2(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {

        System.err.println("******************");
        if (file == null) {
            return "文件不存在";
        }

        String fileName = file.getOriginalFilename();
        //获取文件名后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
            InputStream is = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            //有多少个sheet
            int sheets = workbook.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                //获取多少行
                int rows = sheet.getPhysicalNumberOfRows();
                Eq h = null;
                //遍历每一行，注意：第 0 行为标题
                for (int j = 1; j < rows; j++) {
                    //                for (int j = 1; j < 2; j++) {
                    h = new Eq();
                    XSSFRow row = sheet.getRow(j);

                    row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                    h.setEq_id(row.getCell(3).getStringCellValue());
                    row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    h.setEq_name(row.getCell(2).getStringCellValue());
                    h.setEq_lat(120.1);
                    h.setEq_lon(30.4);
                    row.getCell(9).setCellType(Cell.CELL_TYPE_STRING);
                    row.getCell(10).setCellType(Cell.CELL_TYPE_STRING);
                    h.setEq_account(row.getCell(9).getStringCellValue());
                    h.setEq_account(row.getCell(10).getStringCellValue());
                    row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);

                    WxUser byName = wxUserService.findByName(row.getCell(4).getStringCellValue());
                    if(byName != null){
                        h.setManager_id(byName.getId());
                    }
                    else{
                        h.setManager_id("");
                    }

                    User byAgentNick = userService.findByAgentNick(row.getCell(5).getStringCellValue());
                    if(byAgentNick != null){
                        h.setAgent_id(byAgentNick.getId());
                    }
                    else{
                        h.setAgent_id("");
                    }

                    row.getCell(8).setCellType(Cell.CELL_TYPE_NUMERIC);
                    h.setCard_count((int)row.getCell(8).getNumericCellValue());
//                    Eq eq = appRepository.findOneByEqName(row.getCell(10).getStringCellValue());
                    row.getCell(7).setCellType(Cell.CELL_TYPE_NUMERIC);
                    h.setSecond_price(row.getCell(7).getNumericCellValue());
                    h.setIs_active(false);
                    row.getCell(11).setCellType(Cell.CELL_TYPE_NUMERIC);
                    row.getCell(12).setCellType(Cell.CELL_TYPE_NUMERIC);
                    row.getCell(13).setCellType(Cell.CELL_TYPE_NUMERIC);
                    h.setAgent_proportion((int)row.getCell(11).getNumericCellValue());
                    h.setManager_proportion((int)row.getCell(12).getNumericCellValue());
                    h.setPlatformp_proportion((int)row.getCell(13).getNumericCellValue());

                    Eq insert = mongoTemplate.insert(h);
                    if(insert!=null){
                        sheet.removeRow(row);
                    }
                }

                // 删除空白行
                int lastRowNum = sheet.getLastRowNum();
                for(int k =1;k<lastRowNum;k++){
                    Row r = sheet.getRow(k);
                    Cell cell = null;
                    String cellValue  = "";
                    if (r!=null){
                        cell = r.getCell(0);
                    }
                    if (r==null){
                        sheet.shiftRows(k+1,lastRowNum,-1);
                        k--;
                        //减去一条空行，总行数减一。
                        lastRowNum--;
                    }
                }

//                    FileOutputStream os = new FileOutputStream("C:\\Users\\陈晨\\Desktop\\"+fileName);
                OutputStream os = response.getOutputStream();
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
//                    FileOutputStream os = new FileOutputStream(response.getOutputStream());
//
                workbook.write(os);
                os.close();
            }
            workbook.close();
            is.close();

            return "导入数据成功";

        } else {
            return "文件格式不正确";
        }
    }

    // 卡片导入
    @PostMapping("/import3")
    public String importXXXXXXWords3(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {

        System.err.println("******************");
        if (file == null) {
            return "文件不存在";
        }

        String fileName = file.getOriginalFilename();
        //获取文件名后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
            InputStream is = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            //有多少个sheet
            int sheets = workbook.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                //获取多少行
                int rows = sheet.getPhysicalNumberOfRows();
                Card h = null;
                //遍历每一行，注意：第 0 行为标题
                for (int j = 1; j < rows; j++) {
                    //                for (int j = 1; j < 2; j++) {
                    h = new Card();
                    XSSFRow row = sheet.getRow(j);

                    row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    h.setCardNum(row.getCell(2).getStringCellValue());

                    h.setBelongs("");

                    row.getCell(6).setCellType(Cell.CELL_TYPE_NUMERIC);
                    h.setEffluentTime((int)row.getCell(6).getNumericCellValue());

                    h.setBalance(0.0);

                    h.setType(0);

                    Set map = new HashSet();
                    row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                    Eq byEqName = eqService.findByEqName(row.getCell(5).getStringCellValue());
                    String eqId = "-";
                    String eqName = "-";
                    if(byEqName!=null){
                        eqId = byEqName.getEq_id();
                        map.add(eqId);
                    }
                    h.setEqs(map);

                    Card insert = mongoTemplate.insert(h);
                    if(insert!=null){
                        sheet.removeRow(row);
                    }
                }

                // 删除空白行
                int lastRowNum = sheet.getLastRowNum();
                for(int k =1;k<lastRowNum;k++){
                    Row r = sheet.getRow(k);
                    Cell cell = null;
                    String cellValue  = "";
                    if (r!=null){
                        cell = r.getCell(0);
                    }
                    if (r==null){
                        sheet.shiftRows(k+1,lastRowNum,-1);
                        k--;
                        //减去一条空行，总行数减一。
                        lastRowNum--;
                    }
                }

//                    FileOutputStream os = new FileOutputStream("C:\\Users\\陈晨\\Desktop\\"+fileName);
                OutputStream os = response.getOutputStream();
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
//                    FileOutputStream os = new FileOutputStream(response.getOutputStream());
//
                workbook.write(os);
                os.close();
            }
            workbook.close();
            is.close();

            return "导入数据成功";

        } else {
            return "文件格式不正确";
        }
    }

    // 卡片余额、所属用户、绑定服务站 更新导入
    @PostMapping("/import4")
    public String importXXXXXXWords4(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {

        System.err.println("******************");
        if (file == null) {
            return "文件不存在";
        }

        String fileName = file.getOriginalFilename();
        //获取文件名后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
            InputStream is = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            //有多少个sheet
            int sheets = workbook.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                //获取多少行
                int rows = sheet.getPhysicalNumberOfRows();
                Card h = null;
                //遍历每一行，注意：第 0 行为标题
                for (int j = 1; j < rows; j++) {
                    //                for (int j = 1; j < 2; j++) {
                    h = new Card();
                    XSSFRow row = sheet.getRow(j);

                    row.getCell(6).setCellType(Cell.CELL_TYPE_NUMERIC);

                    Query query = new Query();
                    row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    query.addCriteria(Criteria.where("card_num").is(row.getCell(2).getStringCellValue()));

                    row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                    String s = row.getCell(5).getStringCellValue();
                    if(s.equals("暂未绑定")){
                        s = "";
                    }
                    else{
                        WxUser byName = wxUserService.findByName(s);
                        if(byName!=null){
                            s = byName.getId();
                        }
                        else{
                            s = "";
                        }
                    }

                    Set set = new HashSet();
                    row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                    String e = row.getCell(3).getStringCellValue();
                    Eq byEqName = eqService.findByEqName(e);
                    if(byEqName!=null){
                        set.add(byEqName.getEq_id());
                    }
                    Update update = new Update();
                    update.set("balance",row.getCell(6).getNumericCellValue());
                    update.set("belongs",s);
                    update.set("Eqs",set);
                    UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Card.class);
                    if(updateResult!=null){
                        sheet.removeRow(row);
                    }
                }

                // 删除空白行
                int lastRowNum = sheet.getLastRowNum();
                for(int k =1;k<lastRowNum;k++){
                    Row r = sheet.getRow(k);
                    Cell cell = null;
                    String cellValue  = "";
                    if (r!=null){
                        cell = r.getCell(0);
                    }
                    if (r==null){
                        sheet.shiftRows(k+1,lastRowNum,-1);
                        k--;
                        //减去一条空行，总行数减一。
                        lastRowNum--;
                    }
                }

//                    FileOutputStream os = new FileOutputStream("C:\\Users\\陈晨\\Desktop\\"+fileName);
                OutputStream os = response.getOutputStream();
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
//                    FileOutputStream os = new FileOutputStream(response.getOutputStream());
//
                workbook.write(os);
                os.close();
            }
            workbook.close();
            is.close();

            return "导入数据成功";

        } else {
            return "文件格式不正确";
        }
    }

    // 充值记录
    @PostMapping("/import5")
    public String importXXXXXXWords5(@RequestParam("file") MultipartFile file,@RequestParam(value = "type",required = false) Integer type, HttpServletResponse response) throws Exception {

        System.err.println("******************");
        if (file == null) {
            return "文件不存在";
        }

        String fileName = file.getOriginalFilename();
        //获取文件名后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
            InputStream is = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            //有多少个sheet
            int sheets = workbook.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                //获取多少行
                int rows = sheet.getPhysicalNumberOfRows();
                Order h = null;
                //遍历每一行，注意：第 0 行为标题
                SimpleDateFormat sdf = new SimpleDateFormat("");

                for (int j = 1; j < rows; j++) {
                    //                for (int j = 1; j < 2; j++) {
                    h = new Order();
                    XSSFRow row = sheet.getRow(j);

                    h.setType(type);

                    if(type ==0){
                        row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                        h.setCardNum(row.getCell(3).getStringCellValue());

                        row.getCell(6).setCellType(Cell.CELL_TYPE_NUMERIC);
                        h.setAmount(row.getCell(6).getNumericCellValue());
                        Map<String,Object> map = new HashMap<>();
                        map.put("user_id",null);
                        map.put("user_nick",null);
                        map.put("identity",4);
                        h.setOperator(map);

                        h.setRechargeTime(sdf.parse(row.getCell(5).getStringCellValue()));
                    }
                    if(type ==1){
                        row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                        h.setCardNum(row.getCell(4).getStringCellValue());

                        row.getCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
                        h.setAmount(row.getCell(5).getNumericCellValue());
                        Map<String,Object> map = new HashMap<>();
                        map.put("user_id",null);
                        map.put("user_nick",null);
                        map.put("identity",null);
                        h.setOperator(map);
                        h.setRechargeTime(sdf.parse(row.getCell(6).getStringCellValue()));
                    }

                    Order insert = mongoTemplate.insert(h);
                    if(insert!=null){
                        sheet.removeRow(row);
                    }
                }

                // 删除空白行
                int lastRowNum = sheet.getLastRowNum();
                for(int k =1;k<lastRowNum;k++){
                    Row r = sheet.getRow(k);
                    Cell cell = null;
                    String cellValue  = "";
                    if (r!=null){
                        cell = r.getCell(0);
                    }
                    if (r==null){
                        sheet.shiftRows(k+1,lastRowNum,-1);
                        k--;
                        //减去一条空行，总行数减一。
                        lastRowNum--;
                    }
                }

//                    FileOutputStream os = new FileOutputStream("C:\\Users\\陈晨\\Desktop\\"+fileName);
                OutputStream os = response.getOutputStream();
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
//                    FileOutputStream os = new FileOutputStream(response.getOutputStream());
//
                workbook.write(os);
                os.close();
            }
            workbook.close();
            is.close();

            return "导入数据成功";

        } else {
            return "文件格式不正确";
        }
    }


    // 充值选项导入
    @PostMapping("/import6")
    public String importXXXXXXWords6(@RequestParam("file") MultipartFile file, @RequestParam(value = "type",required = false) Integer type,HttpServletResponse response) throws Exception {

        System.err.println("******************");
        if (file == null) {
            return "文件不存在";
        }

        String fileName = file.getOriginalFilename();
        //获取文件名后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
            InputStream is = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            //有多少个sheet
            int sheets = workbook.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                //获取多少行
                int rows = sheet.getPhysicalNumberOfRows();
                RechargeOpt h = null;
                //遍历每一行，注意：第 0 行为标题
                for (int j = 1; j < rows; j++) {
                    //                for (int j = 1; j < 2; j++) {
                    h = new RechargeOpt();
                    XSSFRow row = sheet.getRow(j);

                    h.setType(type);

                    row.getCell(2).setCellType(Cell.CELL_TYPE_NUMERIC);
                    h.setRechargeAmount((int)row.getCell(2).getNumericCellValue());

                    if(type == 0){
                        h.setGivenAmount(0);
                    }
                    else if(type == 1){
                        row.getCell(3).setCellType(Cell.CELL_TYPE_NUMERIC);
                        h.setGivenAmount((int)row.getCell(3).getNumericCellValue());
                    }




                    RechargeOpt insert = mongoTemplate.insert(h);
                    if(insert!=null){
                        sheet.removeRow(row);
                    }
                }

                // 删除空白行
                int lastRowNum = sheet.getLastRowNum();
                for(int k =1;k<lastRowNum;k++){
                    Row r = sheet.getRow(k);
                    Cell cell = null;
                    String cellValue  = "";
                    if (r!=null){
                        cell = r.getCell(0);
                    }
                    if (r==null){
                        sheet.shiftRows(k+1,lastRowNum,-1);
                        k--;
                        //减去一条空行，总行数减一。
                        lastRowNum--;
                    }
                }

//                    FileOutputStream os = new FileOutputStream("C:\\Users\\陈晨\\Desktop\\"+fileName);
                OutputStream os = response.getOutputStream();
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
//                    FileOutputStream os = new FileOutputStream(response.getOutputStream());
//
                workbook.write(os);
                os.close();
            }
            workbook.close();
            is.close();

            return "导入数据成功";

        } else {
            return "文件格式不正确";
        }
    }

}

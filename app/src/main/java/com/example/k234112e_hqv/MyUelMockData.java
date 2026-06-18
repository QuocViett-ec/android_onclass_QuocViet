package com.example.k234112e_hqv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyUelMockData {
    public static List<TrainingProgram> getTrainingPrograms() {
        List<TrainingProgram> programs = new ArrayList<>();

        List<SemesterProgram> ecommerceSemesters = new ArrayList<>();
        ecommerceSemesters.add(semester("Học kì 1", "hoc ki 1 thuong mai dien tu co ban",
                subject("EC101", "Nhập môn ngành Thương mại điện tử", 3),
                subject("IT101", "Tin học đại cương", 3),
                subject("MA101", "Toán cao cấp", 3),
                subject("LA101", "Pháp luật đại cương", 2),
                subject("EC102", "Kinh tế vi mô", 3)));
        ecommerceSemesters.add(semester("Học kì 2", "hoc ki 2 lap trinh marketing quan tri",
                subject("MK101", "Marketing căn bản", 3),
                subject("IT102", "Cơ sở lập trình", 3),
                subject("EC103", "Kinh tế vĩ mô", 3),
                subject("MG101", "Quản trị học", 3),
                subject("EN101", "Tiếng Anh thương mại", 3)));
        ecommerceSemesters.add(semester("Học kì 3", "hoc ki 3 co so du lieu web ke toan thong ke",
                subject("IT201", "Cơ sở dữ liệu", 3),
                subject("WEB201", "Thiết kế web", 3),
                subject("MK201", "Hành vi người tiêu dùng", 3),
                subject("AC201", "Nguyên lý kế toán", 3),
                subject("ST201", "Thống kê ứng dụng", 3)));

        programs.add(new TrainingProgram(
                "Chương trình đào tạo Thương mại điện tử",
                "https://myuel.uel.edu.vn/Default.aspx?ModuleId=f92f39b2-dea3-4185-8cbb-56c1c49c5226&OlogyID=411&DepartmentID=05&GraduateLevelID=DH&StudyTypeID=CQ",
                Arrays.asList("thương mại điện tử", "ecommerce", "e-commerce", "tmđt", "thuong mai dien tu"),
                ecommerceSemesters));

        List<SemesterProgram> misSemesters = new ArrayList<>();
        misSemesters.add(semester("Học kì 1", "hoc ki 1 he thong thong tin quan ly co ban",
                subject("MIS101", "Nhập môn Hệ thống thông tin quản lý", 3),
                subject("IT101", "Tin học đại cương", 3),
                subject("MA101", "Toán cao cấp", 3),
                subject("LA101", "Pháp luật đại cương", 2),
                subject("EC102", "Kinh tế vi mô", 3)));
        misSemesters.add(semester("Học kì 2", "hoc ki 2 lap trinh co so du lieu quan tri",
                subject("IT102", "Cơ sở lập trình", 3),
                subject("IT201", "Cơ sở dữ liệu", 3),
                subject("MG101", "Quản trị học", 3),
                subject("EC103", "Kinh tế vĩ mô", 3),
                subject("EN101", "Tiếng Anh thương mại", 3)));
        misSemesters.add(semester("Học kì 3", "hoc ki 3 phan tich thiet ke he thong mang may tinh",
                subject("MIS201", "Phân tích thiết kế hệ thống", 3),
                subject("NET201", "Mạng máy tính", 3),
                subject("DB201", "Hệ quản trị cơ sở dữ liệu", 3),
                subject("EC201", "Thương mại điện tử căn bản", 3),
                subject("ST201", "Thống kê ứng dụng", 3)));

        programs.add(new TrainingProgram(
                "Chương trình đào tạo Hệ thống thông tin quản lý",
                "https://myuel.uel.edu.vn/Default.aspx?ModuleId=f92f39b2-dea3-4185-8cbb-56c1c49c5226&OlogyID=7340405&DepartmentID=05&GraduateLevelID=DH&StudyTypeID=CQ",
                Arrays.asList("hệ thống thông tin quản lý", "management information systems", "mis", "htttql", "he thong thong tin quan ly"),
                misSemesters));

        List<SemesterProgram> aiSemesters = new ArrayList<>();
        aiSemesters.add(semester("Học kì 1", "hoc ki 1 kinh doanh so tri tue nhan tao co ban",
                subject("DBA101", "Nhập môn Kinh doanh số", 3),
                subject("AI101", "Nhập môn Trí tuệ nhân tạo", 3),
                subject("IT101", "Tin học đại cương", 3),
                subject("MA101", "Toán cao cấp", 3),
                subject("EC102", "Kinh tế vi mô", 3)));
        aiSemesters.add(semester("Học kì 2", "hoc ki 2 lap trinh marketing so quan tri",
                subject("IT102", "Cơ sở lập trình", 3),
                subject("MK102", "Marketing số", 3),
                subject("MG101", "Quản trị học", 3),
                subject("EC103", "Kinh tế vĩ mô", 3),
                subject("EN101", "Tiếng Anh thương mại", 3)));
        aiSemesters.add(semester("Học kì 3", "hoc ki 3 machine learning phan tich du lieu",
                subject("AI201", "Machine Learning căn bản", 3),
                subject("DA201", "Phân tích dữ liệu kinh doanh", 3),
                subject("IT201", "Cơ sở dữ liệu", 3),
                subject("MK201", "Hành vi người tiêu dùng số", 3),
                subject("ST201", "Thống kê ứng dụng", 3)));

        programs.add(new TrainingProgram(
                "Chương trình đào tạo Kinh doanh số và trí tuệ nhân tạo",
                "https://myuel.uel.edu.vn/Default.aspx?ModuleId=f92f39b2-dea3-4185-8cbb-56c1c49c5226&OlogyID=416&DepartmentID=05&GraduateLevelID=DH&StudyTypeID=CQ",
                Arrays.asList("kinh doanh số", "trí tuệ nhân tạo", "digital business", "artificial intelligence", "ai", "kinh doanh so va tri tue nhan tao"),
                aiSemesters));

        return programs;
    }

    private static SemesterProgram semester(String name, String description, Subject... subjects) {
        return new SemesterProgram(name, Arrays.asList(subjects), description);
    }

    private static Subject subject(String code, String name, int credits) {
        return new Subject(code, name, credits);
    }
}

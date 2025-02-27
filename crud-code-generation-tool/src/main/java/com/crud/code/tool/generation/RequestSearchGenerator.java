package com.crud.code.tool.generation;


import cn.hutool.core.util.StrUtil;
import com.crud.code.tool.config.BaseEntityColumns;
import com.crud.code.tool.config.ColumnInfo;
import com.crud.code.tool.config.GenerateCode;
import com.crud.code.tool.utils.Tools;
import java.util.*;

public class RequestSearchGenerator {

	public static void cal(GenerateCode code) {

		String className = code.getObjectName();

		Map<String, List<String>> importEnum = code.getNewEnums();
		Set<String> importClass = new HashSet<>();

		for (ColumnInfo col : code.getColumns()) { // 把需要声明引入的类找出来
			if (!col.getFieldExists()) {
				continue;
			}
			if (StrUtil.isNotBlank(col.getJavaPackage())) {
				importClass.add(col.getJavaPackage());
			}
		}
		List<String> asList = Arrays.asList(importEnum.keySet().toArray(new String[]{}));

		StringBuffer sb = new StringBuffer();

		sb.append("package ").append(code.getJavaPackage()).append(".").append(code.getModelName()).append(".request;\n")
				.append("\n");

		for (String importString : asList) { // 把需要声明引入的类找出来
			sb.append("import ").append(code.getJavaPackage()).append(".").append(code.getModelName()).append(".enums.").append(importString).append(";\n");
		}
		for (String importString : importClass) {
			sb.append("import ").append(importString).append(";\n");
		}

		sb.append("\n")

				.append("import cn.hutool.json.JSONUtil;\n")
				.append("import ").append(code.getJavaPackage()).append(".").append(code.getModelName()).append(".entity.").append(className).append("Entity;\n")

				.append("import lombok.Data;\n")
				.append("import jakarta.validation.constraints.*;\n")
				.append("import lombok.AllArgsConstructor;\n")
				.append("import lombok.NoArgsConstructor;\n")

				.append("\n")
				.append("/**\n")
				.append(" * ").append(code.getTable().getTableComment()).append(" 查询参数 \n")
				.append(" *\n")
				.append(" */\n")
				.append("@Data\n")
				.append("@AllArgsConstructor\n")
				.append("@NoArgsConstructor\n")
				.append("public class ").append(className).append("SearchRequest {\n\n")
		;

		for (ColumnInfo col : code.getColumns()) {
			if (!col.getFieldExists()) {
				continue;
			}
			if ("_id".equalsIgnoreCase(col.getColumnName()) || "id".equalsIgnoreCase(col.getColumnName())) {
				continue;
			}
			// 复杂查询条件
			if("Object".equalsIgnoreCase(col.getJavaClassName())){
				continue;
			}
			// 如果等于基础字段，重名也要continue,不再生成
			if (BaseEntityColumns.isMetaDataColumn(col)) {
				continue;
			}
			if ("Date".equalsIgnoreCase(col.getJavaColumnName())){
				if (StrUtil.isNotBlank(col.getColumnComment())) {
					sb.append("	/**\n")
							.append("	 *  ").append(col.getColumnComment()).append(" Start").append("\n")
							.append("    */\n");
				}
				sb.append("    private ").append(col.newJavaClassName()).append(" ").append(col.getJavaColumnNameLowwer())
						.append("Start;\n\n");

				if (StrUtil.isNotBlank(col.getColumnComment())) {
					sb.append("	/**\n")
							.append("	 *  ").append(col.getColumnComment()).append(" End").append("\n")
							.append("    */\n");
				}
				sb.append("    private ").append(col.newJavaClassName()).append(" ").append(col.getJavaColumnNameLowwer())
						.append("End;\n\n");

			}else{
				if (StrUtil.isNotBlank(col.getColumnComment())) {
					sb.append("	/**\n")
							.append("	 *  ").append(col.getColumnComment()).append("\n")
							.append("    */\n");
				}
				sb.append("    private ").append(col.newJavaClassName()).append(" ").append(col.getJavaColumnNameLowwer())
						.append(";\n\n");
			}
		}

		sb.append("    public static ").append(className).append("Entity toEntity(").append(className).append("SearchRequest request) {\n")
				.append("        String json = JSONUtil.toJsonStr(request);\n").append("        return JSONUtil.toBean(json, ")
				.append(className).append("Entity.class);\n").append("    }\n");
		sb.append("}\n");


		Tools.log(sb.toString());
		code.setRequestSearchCode(sb.toString());
		code.setRequestSearchCodeFileName(className + "SearchRequest.java");
	}

}

package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jhaws.common.io.FilePath;
import org.jhaws.common.web.resteasy.CustomObjectMapper;

// Magic: color: rgb(61, 142, 185);
// Rare: color: rgb(97, 189, 109);
// Artificer: color: rgb(147, 101, 184);
// Relic: color: rgb(243, 121, 52);
// Archeotech: color: rgb(184, 49, 47);
public class Build implements JSoupHelper {
	public static class PsalmInfo {
		public List<Psalm> getPsalms() {
			return psalms;
		}

		public void setPsalms(List<Psalm> psalms) {
			this.psalms = psalms;
		}

		private List<Psalm> psalms;

		private List<PsalmDoctrine> doctrines;
	}

	public static class PsalmDoctrine {
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<String> getPsalms() {
			return psalms;
		}

		public void setPsalms(List<String> psalms) {
			this.psalms = psalms;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		List<String> psalms;
		String description;
		String id;
	}

	public static class Psalm {
		@Override
		public String toString() {
			return "Psalm [name=" + name + ", description=" + description + ", level=" + level + "]";
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		String name;
		String description;
		String level;
		byte[] image;
	}

	public Build() {
		CustomObjectMapper om = new CustomObjectMapper() {
			{
				configure(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT, true);
			}
		};
		try {
			PsalmInfo info = new PsalmInfo();
			info.psalms = new ArrayList<>();
			soup(true, new String(new FilePath(Build.class, "table.html").readAllBytes()), "tr", e -> e).stream().skip(1)//
					.forEach(e -> {
						Psalm psalm = new Psalm();
						try {
							psalm.name = e.child(1).text();
							psalm.description = e.child(2).text();
							psalm.level = e.child(3).text();
							psalm.image = new FilePath(Build.class, psalm.name + ".png").readAllBytes();
							info.psalms.add(psalm);
						} catch (Exception ex) {
							System.out.println(psalm);
							ex.printStackTrace();
						}
					});
			@SuppressWarnings("unused")
			Map<String, Psalm> map = info.psalms.stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
			info.doctrines = new ArrayList<>();
			soup(true, new String(new FilePath(Build.class, "Warhammer 40,000 Inquisitor - Martyr - Compendium - NeocoreGames.htm").readAllBytes()), "table", e -> e).stream().skip(14).limit(1)//
					.forEach(e -> {
						String h = e.toString();
						soup(true, h, "tr", x -> x).stream().skip(2).forEach(tr -> {
							PsalmDoctrine doctrine = new PsalmDoctrine();
							info.doctrines.add(doctrine);
							doctrine.id = tr.child(0).text();
							doctrine.psalms = Arrays.stream(tr.child(0).text().split("\\+")).map(String::trim).collect(Collectors.toList());
							doctrine.description = tr.child(1).text();
						});
					});
			String jso = JsonHelper._write(om, info);
			System.out.println(jso);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Build();
	}
}

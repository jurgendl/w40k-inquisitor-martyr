interface W40IData {
	psalms: W40IPsalm[];
	doctrines: W40IDoctrine[];
}

interface W40IPsalm {
	name: string;
	description: string;
	level: string;
	image: string;
}

interface W40IDoctrine {
	psalms: string[];
	description: string;
	id: string;
}

export class Component {
	emptyImage = "data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
	defaultSelectedId = 'Phosphoenic Psalm + Technomartyr Psalm + Neuralis Psalm + Binharic Psalm';

	init(): void {
		fetch('assets/w40k-inquisitor-martyr-code.json')
			.then((response) => response.json())
			.then((data) => this.app(data));
	}

	data!: W40IData;

	levelForPsalm(psalm_name: string): string {
		return this.psalmByname(psalm_name)?.level;
	}

	allPsalmsOfLevels(psalm_level: string[]): W40IPsalm[] {
		return (<W40IPsalm[]>this.data.psalms.filter(psalm => psalm_level.includes(psalm.level)));
	}

	psalmByname(psalm_name: string): W40IPsalm {
		return (<W40IPsalm>this.data.psalms.find(psalm => psalm.name == psalm_name));
	}

	isPsalmChecked(psalm_name: string): boolean {
		return window.localStorage.getItem(psalm_name) == 'true';
	}

	checkDoctrineChange(input: HTMLInputElement) {
		const checked: boolean = input.checked;
		const val: string | null = input.getAttribute('value');
		if (val) {
			window.localStorage.setItem(val, String(checked));
		}
		const psalm_name: string | null = input.getAttribute('data-psalm-name');
		if (psalm_name) {
			if (checked) {
				$('.' + psalm_name.replace(' ', '_')).removeClass("strikeme");
			} else {
				$('.' + psalm_name.replace(' ', '_')).addClass("strikeme");
			}
		}
	}

	findDoctrineById(doctrine_id: string): W40IDoctrine {
		return (<W40IDoctrine>this.data.doctrines.find(doctrine => doctrine.id == doctrine_id));
	}

	selectDoctrineChange() {
		const doctrine_id = $("#selectDoctrine").find('option:selected').val();
		if (doctrine_id && typeof doctrine_id === "string") {
			const doctrine = this.findDoctrineById(String(doctrine_id));
			if (doctrine) {
				$("#description").html(doctrine.description);
				for (let i = 0; i < 6; i++) {
					$("#p" + i).attr("src", this.emptyImage).attr("title", "");
					$("#description" + i).attr("class", "").text(" ");
				}
				$.each(doctrine.psalms, (psalm_index: number, psalm_name: string) => {
					const psalm: W40IPsalm | undefined = this.psalmByname(psalm_name);
					if (psalm) {
						$("#p" + psalm_index).attr("src", "data:image/png;base64," + psalm.image).attr("title", psalm.name);
						$("#description" + psalm_index).attr("class", psalm.level).html('<u><b>' + psalm.name + '</b></u>: ' + psalm.description);
					}
				});
			}
		}
	}

	app(data: W40IData) {
		this.data = data;

		for (let i = 0; i < 6; i++) {
			$('#imgcontainer').append('<img id="p' + i + '" width="60" height="60" src="' + this.emptyImage + '">&nbsp;');
			$('#descriptioncontainer').append('<div id="description' + i + '">&nbsp;</div>');
		}

		$('#selectDoctrineContainer').append(
			$('<select/>')
				.attr('id', 'selectDoctrine')
				.attr('size', 'false')
				.attr('name', 'selectDoctrine')
				//.attr('data-live-search', 'true')
				.attr('data-style', 'btn-light')
				.append($('<option>', {}).text(""))
		);

		$.each(data.doctrines, (doctrine_index, doctrine) => {
			let psalmList = '';
			$.each(doctrine.psalms, (psalm_index, psalm_name) => {
				psalmList += '<span><span class="psalm_list ' + psalm_name.replace(' ', '_') + ' ' + this.psalmByname(psalm_name)?.level + '">' + psalm_name + '</span></span>';
			});
			$('#selectDoctrine').append($('<option>', {
				'value': doctrine.id //
				, 'data-content': //
					'<div style="font-weight: bold;">' + doctrine.description + '</div>' +//
					'<div class="psalm_list_parent" style="font-weight: bold; font-size: smaller;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + psalmList + '</div>'
			}).text(doctrine.description));
		});

		(<HTMLInputElement>document.querySelector('#selectDoctrine')).value = this.defaultSelectedId;

		// https://developer.snapappointments.com/bootstrap-select/
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		($('#selectDoctrine') as any).selectpicker();

		$.each(this.allPsalmsOfLevels(['Relic', 'Archeotech']), (psalm_index: number, psalm: W40IPsalm) => {
			$('#pills-settings').append(
				'<div class="imgcheck input-group input-group-sm">' +
				'<label class="checkbox-inline" for="check' + psalm_index + '">' +
				'<input ' + (this.isPsalmChecked(psalm.name) ? ' checked="checked"' : '') + ' data-psalm-name="' + psalm.name + '" value="' + psalm.name + '" style="display: none;" type="checkbox" id="check' + psalm_index + '" />' +
				'<img id="is' + psalm_index + '" width="60" height="60" src="data:image/png;base64, ' + psalm.image + '">' +
				'&nbsp;<span class="' + psalm.name.replace(' ', '_') + (this.isPsalmChecked(psalm.name) ? '' : ' strikeme') + '" id="lbl' + psalm_index + '">' + psalm.name + '</span>' +
				'</label>' +
				'</div>'
			);
			$('#check' + psalm_index).change((evt: JQuery.ChangeEvent<HTMLElement, null, HTMLElement, HTMLElement>) => this.checkDoctrineChange(evt.target as HTMLInputElement));
		});

		console.log("before selectDoctrineChange");
		$("#selectDoctrine").change(() => this.selectDoctrineChange());
		this.selectDoctrineChange();

		setTimeout(() => {
			$('[data-id="selectDoctrine"]').click();
			setTimeout(() => {
				$('[data-id="selectDoctrine"]').click();
				$.each(this.allPsalmsOfLevels(['Relic', 'Archeotech']), (psalm_index: number, psalm: W40IPsalm) => {
					if (!this.isPsalmChecked(psalm.name))
						$('.' + psalm.name.replace(' ', '_')).addClass("strikeme");
				});
			}, 500);
		}, 1000);
	}
}

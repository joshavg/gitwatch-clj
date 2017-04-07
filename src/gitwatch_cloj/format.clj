(ns gitwatch-cloj.format)

; now unused - but code to learn from in future times

;╔════════════╤═════════╤════════╤══════════╤═══════╤════════╗
;║ Name       │ Branch  │ Status │ Modified │ Ahead │ Behind ║
;╠════════════╪═════════╪════════╪══════════╪═══════╪════════╣
;║ rbm        │ develop │ ✓      │ 0        │ 1     │ 0      ║
;╟────────────┼─────────┼────────┼──────────┼───────┼────────╢
;║ satbackend │ master  │ ✓      │ 0        │ 1     │ 0      ║
;╟────────────┼─────────┼────────┼──────────┼───────┼────────╢
;║ stage      │ master  │ ✗      │ 0        │ 0     │ 0      ║
;╟────────────┼─────────┼────────┼──────────┼───────┼────────╢
;║ vagrant    │ master  │ ✗      │ 0        │ 0     │ 0      ║
;╚════════════╧═════════╧════════╧══════════╧═══════╧════════╝


(defn maxwidth
    [header content]
    (apply max
           (map #(count (str %))
                (conj
                    (map #((keyword header) %)
                         content)
                    header))))

(def maxwidth-memo
    (memoize maxwidth))

(defn strrep
    [c s]
    (if (< c 0)
        ""
        (clojure.string/join (repeat c s))))

(defn create-line
    [type cols rows]
    (let [start (case type 1 "╔"
                      2      "╠"
                      3      "╟"
                      4      "╚")
          mid   (case type 1 "╤"
                      2      "╪"
                      3      "┼"
                      4      "╧")
          end   (case type 1 "╗"
                      2      "╣"
                      3      "╢"
                      4      "╝")
          line  (case type 1 "═"
                      2      "═"
                      3      "─"
                      4      "═")]
        (str
            (reduce
                (fn [s curr]
                    (str s
                         (if (empty? s) start mid)
                         line line
                         (strrep (maxwidth-memo curr rows) line)))
                ""
                cols)
            end)))

(defn table-header
    [cols rows]
    (str
        (create-line 1 cols rows)
        "\n"
        (reduce
            #(str %1
              (if (empty? %1) "║" "│")
              " "
              %2
              (strrep (- (maxwidth-memo %2 rows) (count %2)) " ")
              " ")
            ""
            cols)
        "║\n"
        (create-line 2 cols rows)))

(defn content-row
    [row cols rows]
    (str
        (reduce
            (fn [s col]
                (let [colval ((keyword col) row)
                      mw     (maxwidth-memo col rows)
                      rpad   (strrep (- mw (count colval)) " ")]
                    (str
                        s
                        (if (empty? s) "║" "│")
                        " "
                        colval
                        " "
                        rpad)))
            ""
            cols)
        "║"))

(defn format-table
    [cols rows]
    (let [headerrow    (table-header cols rows)
          content-rows (reduce #(conj %1 (content-row %2 cols rows))
                               []
                               rows)
          bodylines    (clojure.string/join
                           (str "\n" (create-line 3 cols rows) "\n")
                           content-rows)]
        (str
            headerrow
            (when (> (count rows) 0)
                  (str "\n" bodylines))
            "\n"
            (create-line 4 cols rows))))
